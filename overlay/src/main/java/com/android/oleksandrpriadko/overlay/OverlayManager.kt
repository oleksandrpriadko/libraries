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
import com.android.oleksandrpriadko.overlay.OverlayState.ANIMATING_IN
import com.android.oleksandrpriadko.overlay.OverlayState.ANIMATING_OUT
import com.android.oleksandrpriadko.overlay.OverlayState.ANIMATING_OUT_BACK_CLICK
import com.android.oleksandrpriadko.overlay.OverlayState.DISMISSED
import com.android.oleksandrpriadko.overlay.OverlayState.DISMISSED_BACK_CLICK
import com.android.oleksandrpriadko.overlay.OverlayState.DISPLAYING
import com.android.oleksandrpriadko.overlay.OverlayState.IDLE

open class OverlayManager(containerViewGroup: ViewGroup) {

    private val overlayList = mutableListOf<Overlay>()

    private val overlayRenderer: OverlayRenderer

    private val handlerHideInLoop = Handler()

    @get:VisibleForTesting
    internal var isHidingAll = false
        private set

    private var isHostResumed = false

    var showParentBefore: Boolean = true
        get
        /**
         * If true - parent's layout visibility will be changed to [View.VISIBLE] once
         * [Overlay] attached, false - no changes to parent's layout visibility
         */
        set
    var hideParentAfter = true
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

            override fun onAnimateShow(overlay: Overlay) {
                log(message = "onAnimateShow: ${getTagOf(overlay)}")
                overlay.updateState(ANIMATING_IN)
            }

            override fun onDisplay(overlay: Overlay) = toDisplay(overlay, true)

            override fun onAnimateDismiss(overlay: Overlay, hideMethod: HideMethod) {
                log(message = "onAnimateDismiss: ${getTagOf(overlay)}")
                if (hideMethod == CLICK_ON_BACKGROUND) {
                    overlay.updateState(ANIMATING_OUT_BACK_CLICK)
                } else {
                    overlay.updateState(ANIMATING_OUT)
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
     * @param displayNext if true - next [Overlay] in [.overlayList]
     * will be notified
     */
    @VisibleForTesting
    internal fun toDisplay(overlay: Overlay, displayNext: Boolean) {
        log(message = "onDisplay: ${getTagOf(overlay)}")
        overlay.updateState(DISPLAYING)
        if (displayNext) {
            requestDisplayNext(overlay)
        }
    }

    private fun toDismiss(overlay: Overlay, hideMethod: HideMethod) {
        log(message = "toDismissAfterAnimOut: ${getTagOf(overlay)}")
        // covers cases
        // 1) current Overlay is in state ANIMATING_OUT
        // 2) attach() triggered
        requestDisplayNext(overlay)

        stateDismissAndRemoveFromListAndViewGroup(overlay, hideMethod)

        if (areAllHidden()) {
            overlayRenderer.showHoldersContainerViewGroup(!hideParentAfter)
        }

        log(message = "size after hide ${overlayList.size}")

        unlock()
    }

    @VisibleForTesting
    internal fun unlock() {
        if (areAllHidden()) {
            log(message = "isHidingAll = false")
            isHidingAll = false
        }
    }

    @VisibleForTesting
    internal fun setIsDismissingAll(isDismissingAll: Boolean) {
        isHidingAll = isDismissingAll
    }

    fun areAllHidden(): Boolean = overlayList.isEmpty()

    fun isOverlayOnTop(overlay: Overlay): Boolean =
            if (areAllHidden()) false
            else overlayList[overlayList.size - 1] === overlay

    fun isOverlayOnTop(rootViewGroupInOverlayHolder: ViewGroup?): Boolean =
            if (areAllHidden()) false
            else overlayList[overlayList.size - 1].rootViewGroup === rootViewGroupInOverlayHolder

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
    fun add(overlay: Overlay) {
        if (isHidingAll) {
            log(message = "addOverlay: dismissAll in progress")
            return
        }

        val stateValidToCommit = isStateValidToCommit(overlay)
        val isAlreadyAdded = hasSameOverlay(overlay)

        if (stateValidToCommit && !isAlreadyAdded) {
            attach(overlay)
        } else {
            log(message = "addOverlay: cannot proceed with used overlay")
        }
    }

    private fun isStateValidToCommit(overlay: Overlay): Boolean
            = overlay.isReadyToDisplay()

    private fun hasSameOverlay(overlay: Overlay): Boolean
            = overlayList.contains(overlay)

    /**
     * 1) Shows container of all [Overlay]s if necessary;
     * 2) Add provided [Overlay] to [.overlayList]
     * 3) If previous [Overlay] is in #DISPLAYING state -
     * [OverlayRenderer.display];
     * 4) If current [Overlay] is alone - [OverlayRenderer.display]
     */
    private fun attach(overlay: Overlay) {
        overlayList.add(overlay)
        log(message = "attach: ${getTagOf(overlay)}")

        val indexOfAddedTransaction = overlayList.size - 1

        overlayRenderer.showHoldersContainerViewGroup(showParentBefore)

        if (overlayList.size > 1) {
            val previousOverlayHolder = overlayList[indexOfAddedTransaction - 1]
            if (previousOverlayHolder.state == DISPLAYING) {
                overlayRenderer.display(overlay)
            }
        } else {
            overlayRenderer.display(overlay)
        }
    }

    /**
     * If we have next [Overlay] after focusedOverlay in #IDLE state - display it
     */
    @VisibleForTesting
    internal fun requestDisplayNext(focusedOverlay: Overlay) {
        val indexOfLastDisplayed = overlayList.indexOf(focusedOverlay)

        if (overlayList.size > indexOfLastDisplayed + 1) {
            val nextOverlayHolder = overlayList[indexOfLastDisplayed + 1]
            if (nextOverlayHolder.isReadyToDisplay()) {
                log(message = "requestDisplayNext:")
                overlayRenderer.display(nextOverlayHolder)
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

        return dismissLatestOverlayHolder(hideMethod)
    }

    private fun dismissLatestOverlayHolder(hideMethod: HideMethod): Boolean {
        val size = overlayList.size
        val latestOverlayHolder = overlayList[size - 1]

        if (hideMethod == BACK_PRESSED) {
            if (!latestOverlayHolder.doHideByBackPressed) {
                log(message = "$latestOverlayHolder not allowed to be hidden by back press")
                return true
            }
        }

        log(message = "hideLastOverlay:")

        when (latestOverlayHolder.state) {
            ANIMATING_IN, DISPLAYING -> {
                log(message = "dismissLatestOverlayHolder:allowed ${getTagOf(latestOverlayHolder)}")
                overlayRenderer.hide(latestOverlayHolder, false, hideMethod)
            }
            ANIMATING_OUT, ANIMATING_OUT_BACK_CLICK ->
                log(message = "dismissLatestOverlayHolder: animOutInProgress ${getTagOf(latestOverlayHolder)}")
            IDLE, DISMISSED, DISMISSED_BACK_CLICK -> {
                log(message = "dismissLatestOverlayHolder:justRemovedFromList ${getTagOf(latestOverlayHolder)}")
                overlayList.remove(latestOverlayHolder)
                return overlayList.size > 0
            }
        }

        return size > 0
    }

    @VisibleForTesting
    internal fun stateDismissAndRemoveFromListAndViewGroup(overlay: Overlay,
                                                           hideMethod: HideMethod) {
        log(message = "dismissAndRemove${getTagOf(overlay)}")

        overlayRenderer.removeFromHoldersContainerViewGroup(overlay)

        overlayList.remove(overlay)

        if (hideMethod == CLICK_ON_BACKGROUND) {
            overlay.updateState(DISMISSED_BACK_CLICK)
        } else {
            overlay.updateState(DISMISSED)
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

        dismissFromEndInLoop(overlayList.size, handlerHideInLoop, HIDE_ALL)
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
                ANIMATING_IN, DISPLAYING -> {
                    if (indexOfLastActive == -1) {
                        indexOfLastActive = i
                    }
                    val delay = ((indexOfLastActive - i) * DELAY_BETWEEN_ITEMS_WHILE_HIDE_ALL_MS).toLong()
                    log(message = "dismissFromEndInLoop:delayed by $delay${getTagOf(overlayHolder)}")
                    handlerPopInLoop.postDelayed({ overlayRenderer.hide(overlayHolder, !isHostResumed, hideMethod) }, delay)
                }
                ANIMATING_OUT, ANIMATING_OUT_BACK_CLICK ->
                    if (!isHostResumed) {
                        val hideMethodForAnimOut: HideMethod =
                                if (overlayHolder.state == ANIMATING_OUT_BACK_CLICK) CLICK_ON_BACKGROUND
                                else hideMethod

                        overlayRenderer.hide(overlayHolder, !isHostResumed, hideMethodForAnimOut)
                        log(message = "dismissFromEndInLoop: hide immediately")
                    } else {
                        log(message = "dismissFromEndInLoop: animating out")
                    }
                DISMISSED, DISMISSED_BACK_CLICK -> {
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
            suspendWhileDismissingAll(handlerHideInLoop)
            return
        }

        val overlayHolder = findFirstWithState(ANIMATING_IN)
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
    internal fun requestForcedDismissWithRenderer(overlay: Overlay) {
        when (overlay.state) {
            ANIMATING_OUT, DISPLAYING -> {
                log(message = "suspendWhileDismissingAll: forced hide and cancel out anim${getTagOf(overlay)}")
                overlayRenderer.hide(overlay, forceAnimation = true, hideMethod = DEFAULT)
            }
            ANIMATING_OUT_BACK_CLICK -> {
                log(message = "suspendWhileDismissingAll: forced hide and cancel out anim${getTagOf(overlay)}")
                overlayRenderer.hide(overlay, forceAnimation = true, hideMethod = CLICK_ON_BACKGROUND)
            }
            else ->
                log(message = "suspendWhileDismissingAll: no cancel, state=${overlay.state}${getTagOf(overlay)}")
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

        val animatingOverlay = findFirstWithState(ANIMATING_IN, ANIMATING_OUT)
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

    private fun getTagOf(overlay: Overlay): String = overlay.toString()

    companion object {
        private const val DELAY_BETWEEN_ITEMS_WHILE_HIDE_ALL_MS = 300

        private const val isLoggingEnabled = true

        fun log(message: String) {
            if (isLoggingEnabled) {
                CoreServiceManager.logService.e(OverlayManager::class.java.simpleName, message)
            }
        }
    }
}
