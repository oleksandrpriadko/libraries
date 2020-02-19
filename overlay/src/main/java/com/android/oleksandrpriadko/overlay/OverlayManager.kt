package com.android.oleksandrpriadko.overlay

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import com.android.oleksandrpriadko.core.CoreServiceManager
import com.android.oleksandrpriadko.overlay.HideMethod.BACK_PRESSED
import com.android.oleksandrpriadko.overlay.HideMethod.CLICK_ON_BACKGROUND
import com.android.oleksandrpriadko.overlay.HideMethod.DEFAULT
import com.android.oleksandrpriadko.overlay.HideMethod.HIDE_ALL
import com.android.oleksandrpriadko.overlay.OverlayState.*

open class OverlayManager(containerViewGroup: ViewGroup) {

    private val overlayList = mutableListOf<Overlay>()

    /**
     * if we try to add a new overlay while isHidingAll = true, we need to process this
     * overlayHolder when all overlays have been hidden, so add this overlay to this list
     * when this happens
     */
    private val pendingOverlayList = mutableListOf<Overlay>()

    private val overlayRenderer: OverlayRenderer

    private val handlerDismissInLoop = Handler()

    @get:VisibleForTesting
    internal var isHidingAll = false
        private set

    private var isHostResumed = false

    private var showParentBefore: Boolean = true
        get
        /**
         * If true - parent's layout visibility will be changed to [View.VISIBLE] once
         * [Overlay] attached, false - no changes to parent's layout visibility
         */
        set
    private var hideParentAfter = true
        get
        /**
         * If true - parent's layout visibility will be changed to [View.GONE] once
         * all [Overlay]s dismissed, false - no changes to parent's layout visibility
         */
        set

    @VisibleForTesting
    internal val forTestOverlayList: MutableList<Overlay>
        get() = overlayList

    init {
        val rendererListener = object : OverlayRenderer.RendererListener {

            override fun onDisplayInProgress(overlay: Overlay) {
                log(message = "onDisplayInProgress: ${getTagOf(overlay)}")
                overlay.updateState(DISPLAY_IN_PROGRESS)
            }

            override fun onDisplay(overlay: Overlay) = toDisplay(overlay, true)

            override fun onDismissInProgress(overlay: Overlay, hideMethod: HideMethod) {
                log(message = "onDismissInProgress: ${getTagOf(overlay)}")
                if (hideMethod == CLICK_ON_BACKGROUND) {
                    overlay.updateState(DISMISS_IN_PROGRESS_BACKGROUND_CLICK)
                } else {
                    overlay.updateState(DISMISS_IN_PROGRESS)
                }
            }

            override fun onDismiss(overlay: Overlay, hideMethod: HideMethod)
                    = toDismiss(overlay, hideMethod)

            override fun onBackgroundClicked(overlay: Overlay) {
                log(message = "onBackgroundClicked:${getTagOf(overlay)}")
                hideLast(CLICK_ON_BACKGROUND)
            }
        }
        overlayRenderer = OverlayRenderer(containerViewGroup, rendererListener)
    }

    /**
     * @param displayNext if true - next [Overlay] in [.overlayHolderList]
     * will be notified
     */
    @VisibleForTesting
    internal fun toDisplay(overlayHolder: Overlay, displayNext: Boolean) {
        log(message = "onDisplay: ${getTagOf(overlayHolder)}")
        overlayHolder.updateState(DISPLAYING)
        if (displayNext) {
            requestDisplayNext(overlayHolder)
        }
    }

    private fun toDismiss(overlayHolder: Overlay, hideMethod: HideMethod) {
        log(message = "toDismissAfterAnimOut: ${getTagOf(overlayHolder)}")
        // covers cases
        // 1) current Overlay is in state DISMISS_IN_PROGRESS
        // 2) attach() triggered
        requestDisplayNext(overlayHolder)

        stateDismissAndRemoveFromListAndViewGroup(overlayHolder, hideMethod)

        if (areAllHidden()) {
            overlayRenderer.showHoldersContainerViewGroup(!hideParentAfter)
        }

        log(message = "size after hide ${overlayList.size}")

        unlock()
    }

    @VisibleForTesting
    internal fun unlock() {
        if (areAllHidden()) {
            log(message = "unlock: isHidingAll = false")
            isHidingAll = false
            //check docs for pendingOverlayList
            for (overlayHolder in pendingOverlayList) {
                log(message = "unlock: add overlay from pending list")
                add(overlayHolder)
            }
            pendingOverlayList.clear()
        }
    }


    @VisibleForTesting
    internal fun setIsDismissingAll(isDismissingAll: Boolean) {
        isHidingAll = isDismissingAll
    }

    fun areAllHidden(): Boolean = overlayList.isEmpty()

    fun isOverlayOnTop(overlayHolder: Overlay): Boolean =
            if (areAllHidden()) false
            else overlayList[overlayList.size - 1] === overlayHolder

    fun isOverlayOnTop(rootViewGroupInOverlay: ViewGroup?): Boolean =
            if (areAllHidden()) false
            else overlayList[overlayList.size - 1].rootViewGroup === rootViewGroupInOverlay

    fun getTopView() : View? {
        return if (areAllHidden()) {
            null
        } else {
            overlayList[overlayList.size - 1].rootViewGroup
        }
    }
    /**
     * Check validity of [Overlay] and [.attach] if valid
     */
    fun add(overlayHolder: Overlay) {
        if (isHidingAll) {
            log(message = "addOverlay: dismissAll in progress")
            //check docs for pendingOverlayList
            pendingOverlayList.add(overlayHolder)
            log(message = "addOverlay: adding to pending list")
            return
        }

        val stateValidToCommit = isStateValidToCommit(overlayHolder)
        val isAlreadyAdded = hasSameOverlay(overlayHolder)

        if (stateValidToCommit && !isAlreadyAdded) {
            attach(overlayHolder)
        } else {
            log(message = "addOverlay: cannot proceed with used overlayHolder")
        }
    }

    private fun isStateValidToCommit(overlayHolder: Overlay): Boolean
            = overlayHolder.isReadyToDisplay()

    private fun hasSameOverlay(overlayHolder: Overlay): Boolean
            = overlayList.contains(overlayHolder)

    /**
     * 1) Shows container of all [Overlay]s if necessary;
     * 2) Add provided [Overlay] to [.overlayHolderList]
     * 3) If previous [Overlay] is in #DISPLAYING state -
     * [OverlayRenderer.display];
     * 4) If current [Overlay] is alone - [OverlayRenderer.display]
     */
    private fun attach(overlayHolder: Overlay) {
        overlayList.add(overlayHolder)
        log(message = "attach: ${getTagOf(overlayHolder)}")

        val indexOfAddedTransaction = overlayList.size - 1

        overlayRenderer.showHoldersContainerViewGroup(showParentBefore)

        if (overlayList.size > 1) {
            val previousOverlay = overlayList[indexOfAddedTransaction - 1]
            if (previousOverlay.state == DISPLAYING) {
                overlayRenderer.display(overlayHolder)
            }
        } else {
            overlayRenderer.display(overlayHolder)
        }
    }

    /**
     * If we have next [Overlay] after focusedOverlay in #IDLE state - display it
     */
    @VisibleForTesting
    internal fun requestDisplayNext(focusedOverlay: Overlay) {
        val indexOfLastDisplayed = overlayList.indexOf(focusedOverlay)

        if (overlayList.size > indexOfLastDisplayed + 1) {
            val nextOverlay = overlayList[indexOfLastDisplayed + 1]
            if (nextOverlay.isReadyToDisplay()) {
                log(message = "requestDisplayNext:")
                overlayRenderer.display(nextOverlay)
            } else {
                log("overlay not ready to display")
            }
        }
    }

    /**
     * @return true - if queue has [Overlay] after hideLast, false - otherwise
     */
    fun hideLast(hideMethod: HideMethod): Boolean {
        val size = overlayList.size
        if (isHidingAll) {
            log(message = "hideLastOverlay:dismissAll in progress")
            return size > 0
        }

        if (size <= 0) {
            log(message = "hideLastOverlay: disallowed")
            return false
        }

        return dismissLatestOverlay(hideMethod)
    }

    private fun dismissLatestOverlay(hideMethod: HideMethod): Boolean {
        val size = overlayList.size
        val latestOverlay = overlayList[size - 1]

        if (hideMethod == BACK_PRESSED) {
            if (!latestOverlay.doHideByBackPressed) {
                log(message = "$latestOverlay not allowed to be hidden by back press")
                return true
            }
        }

        log(message = "hideLastOverlay:")

        when (latestOverlay.state) {
            DISPLAY_IN_PROGRESS, DISPLAYING -> {
                log(message = "dismissLatestOverlay:allowed ${getTagOf(latestOverlay)}")
                overlayRenderer.hide(latestOverlay, false, hideMethod)
            }
            DISMISS_IN_PROGRESS, DISMISS_IN_PROGRESS_BACKGROUND_CLICK ->
                log(message = "dismissLatestOverlay: animOutInProgress ${getTagOf(latestOverlay)}")
            IDLE, DISMISSED, DISMISSED_BACKGROUND_CLICK -> {
                log(message = "dismissLatestOverlay:justRemovedFromList ${getTagOf(latestOverlay)}")
                overlayList.remove(latestOverlay)
                return overlayList.size > 0
            }
        }

        return size > 0
    }

    @VisibleForTesting
    internal fun stateDismissAndRemoveFromListAndViewGroup(overlayHolder: Overlay,
                                                           hideMethod: HideMethod) {
        log(message = "dismissAndRemove${getTagOf(overlayHolder)}")

        overlayRenderer.removeFromHoldersContainerViewGroup(overlayHolder)

        overlayList.remove(overlayHolder)

        if (hideMethod == CLICK_ON_BACKGROUND) {
            overlayHolder.updateState(DISMISSED_BACKGROUND_CLICK)
        } else {
            overlayHolder.updateState(DISMISSED)
        }
    }

    fun hideAll() {
        if (isHidingAll) {
            log(message = "hideAllOverlays:already in progress")
            return
        }

        val size = overlayList.size
        if (size <= 0) {
            return
        }

        if (size == 1) {
            log(message = "hideAllOverlays:only one item - redirect to " +
                    "simple hideLast with dismiss method DEFAULT")
            hideLast(DEFAULT)
            return
        }

        log(message = "hideAllOverlays")
        isHidingAll = true

        dismissFromEndInLoop(overlayList.size, handlerDismissInLoop, HIDE_ALL)
    }

    @VisibleForTesting
    internal fun dismissFromEndInLoop(size: Int, handlerPopInLoop: Handler, hideMethod: HideMethod) {
        var indexOfLastActive = -1

        for (i in size - 1 downTo 0) {
            val overlayHolder = overlayList[i]
            when (overlayHolder.state) {
                IDLE -> {
                    log(message = "dismissFromEndInLoop:toDismiss ${getTagOf(overlayHolder)}")
                    toDismiss(overlayHolder, hideMethod)
                }
                DISPLAY_IN_PROGRESS, DISPLAYING -> {
                    if (indexOfLastActive == -1) {
                        indexOfLastActive = i
                    }
                    val delay = ((indexOfLastActive - i) * DELAY_BETWEEN_ITEMS_WHILE_POP_ALL_MS).toLong()
                    log(message = "dismissFromEndInLoop:delayed by $delay${getTagOf(overlayHolder)}")
                    handlerPopInLoop.postDelayed({ overlayRenderer.hide(overlayHolder, !isHostResumed, hideMethod) }, delay)
                }
                DISMISS_IN_PROGRESS, DISMISS_IN_PROGRESS_BACKGROUND_CLICK ->
                    if (!isHostResumed) {
                        val hideMethodForAnimOut: HideMethod =
                                if (overlayHolder.state == DISMISS_IN_PROGRESS_BACKGROUND_CLICK) CLICK_ON_BACKGROUND
                                else hideMethod

                        overlayRenderer.hide(overlayHolder, !isHostResumed, hideMethodForAnimOut)
                        log(message = "dismissFromEndInLoop: hide immediately")
                    } else {
                        log(message = "dismissFromEndInLoop: animating out")
                    }
                DISMISSED, DISMISSED_BACKGROUND_CLICK -> {
                }
            }
        }
    }

    /**
     * Call it before going to background. e.g. onStop()
     */
    fun stop() {
        hostStopped()
        if (isHidingAll) {
            suspendWhileDismissingAll(handlerDismissInLoop)
            return
        }

        val overlayHolder = findFirstWithState(DISPLAY_IN_PROGRESS)
        if (overlayHolder == null) {
            log(message = "suspendOverlayManager:disallowed, no animIn Overlay")
            return
        }

        cancelInAnimAndDisplay(overlayHolder)
    }

    private fun cancelInAnimAndDisplay(latestActiveOverlay: Overlay) {
        log(message = "suspendOverlayManager:animIn")
        overlayRenderer.requestCancelAnimations(latestActiveOverlay, true)
        toDisplay(latestActiveOverlay, false)
    }

    @VisibleForTesting
    internal fun suspendWhileDismissingAll(handlerDismissInLoop: Handler) {
        // cancel all pending hide operations
        handlerDismissInLoop.removeCallbacksAndMessages(null)
        // reversed loop as we hideLast from the end
        for (i in overlayList.indices.reversed()) {
            val overlayHolder = overlayList[i]
            requestForcedDismissWithRenderer(overlayHolder)
        }
    }

    @VisibleForTesting
    internal fun requestForcedDismissWithRenderer(overlayHolder: Overlay) {
        when (overlayHolder.state) {
            DISMISS_IN_PROGRESS, DISPLAYING -> {
                log(message = "suspendWhileDismissingAll: forced hide and cancel out anim${getTagOf(overlayHolder)}")
                overlayRenderer.hide(overlayHolder, forceAnimation = true, hideMethod = DEFAULT)
            }
            DISMISS_IN_PROGRESS_BACKGROUND_CLICK -> {
                log(message = "suspendWhileDismissingAll: forced hide and cancel out anim${getTagOf(overlayHolder)}")
                overlayRenderer.hide(overlayHolder, forceAnimation = true, hideMethod = CLICK_ON_BACKGROUND)
            }
            else ->
                log(message = "suspendWhileDismissingAll: no cancel, state=${overlayHolder.state}${getTagOf(overlayHolder)}")
        }
    }

    /**
     * Call it before going to foreground. e.g. onResume()
     */
    fun resume() {
        hostResumed()
        if (isHidingAll) {
            log(message = "resumeOverlayManager: hideAllOverlays in progress")
            return
        }

        val animatingOverlay = findFirstWithState(DISPLAY_IN_PROGRESS, DISMISS_IN_PROGRESS)
        if (animatingOverlay == null) {
            val readyToDisplayOverlay = findReadyToDisplay()

            if (readyToDisplayOverlay != null) {
                log(message = "resumeOverlayManager: display $readyToDisplayOverlay")
                overlayRenderer.display(readyToDisplayOverlay)
            } else {
                log(message = "resumeOverlayManager: disallowed, no idle overlay")
            }
        } else {
            log("resumeOverlayManager: disallowed, $animatingOverlay is animating, " +
                    "once his animation done - he will ask next overlay to display")
        }
    }

    @VisibleForTesting
    internal fun findReadyToDisplay(): Overlay? {
        for (overlayHolder in overlayList) {
            if (overlayHolder.isReadyToDisplay()) {
                return overlayHolder
            }
        }

        return null
    }

    @VisibleForTesting
    internal fun findFirstWithState(vararg statesToCompare: OverlayState): Overlay? {
        for (overlayHolder in overlayList) {
            for (overlayState in statesToCompare) {
                if (overlayHolder.state == overlayState) {
                    return overlayHolder
                }
            }
        }

        return null
    }

    /**
     * Call it before going to be destroyed. e.g. onDestroy()
     */
    fun destroy() {
        log(message = "destroyOverlayManager:")
        stop()

        overlayList.clear()
    }

    @VisibleForTesting
    internal fun hostResumed() {
        isHostResumed = true
    }

    @VisibleForTesting
    internal fun hostStopped() {
        isHostResumed = false
    }

    private fun getTagOf(overlayHolder: Overlay): String = overlayHolder.toString()

    companion object {
        private const val DELAY_BETWEEN_ITEMS_WHILE_POP_ALL_MS = 300

        private const val isLoggingEnabled = true

        fun log(message: String) {
            if (isLoggingEnabled) {
                CoreServiceManager.logService.e(OverlayManager::class.java.simpleName, message)
            }
        }
    }
}
