package com.android.oleksandrpriadko.overlay

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.annotation.VisibleForTesting
import com.android.oleksandrpriadko.extension.hasParent
import com.android.oleksandrpriadko.overlay.OverlayState.IDLE

class Overlay private constructor(internal val rootViewGroup: ViewGroup,
                                  internal val animShowRoot: Animation?,
                                  internal val animHideRoot: Animation?,
                                  internal val backgroundView: View?,
                                  internal val animShowBackground: Animation?,
                                  internal val animHideBackground: Animation?,
                                  internal val contentView: View?,
                                  internal val animShowContent: Animation?,
                                  internal val animHideContent: Animation?,
                                  private val overlayListener: OverlayListener?,
                                  /**
                                         * If true - will be dismissed once user touches backgroundView,
                                         * false - no action once backgroundView touched
                                         */
                                        internal val doHideByClickOnBackground: Boolean,
                                  internal val doHideByBackPressed: Boolean,
                                  private val idForDebug: String) {

    internal var state = IDLE
        private set

    internal fun updateState(state: OverlayState) {
        this.state = state
        log("newState: $state${toString()}")
        overlayListener?.stateChanged(this.state)
    }

    fun isReadyToDisplay(): Boolean = state == IDLE && !rootViewGroup.hasParent()

    override fun toString(): String = " Overlay{$idForDebug}"

    companion object {
        fun log(message: String) {
            OverlayManager.log("${Overlay::class.java.simpleName} $message")
        }
    }

    class Builder(private val rootViewGroup: ViewGroup) {

        private var idForDebug = ""
        private var animShowRoot: Animation? = null
        private var animHideRoot: Animation? = null

        @VisibleForTesting
        internal var backgroundView: View? = null
        private var animShowBackground: Animation? = null
        private var animHideBackground: Animation? = null

        @VisibleForTesting
        internal var contentView: View? = null
        private var animShowContent: Animation? = null
        private var animHideContent: Animation? = null

        private var overlayListener: OverlayListener? = null

        /**
         * check [Overlay.doHideByClickOnBackground]
         */
        private var doHideByClickOnBackground = true

        /**
         * check [Overlay.doHideByBackPressed]
         */
        private var doHideByBackPressed = true

        constructor(rootViewGroup: ViewGroup, idForDebug: String) : this(rootViewGroup) {
            this.idForDebug = idForDebug
        }

        fun animationShowRoot(animationInRoot: Animation?): Builder {
            animShowRoot = animationInRoot
            return this
        }

        fun animationShowRoot(@AnimRes animationInResRoot: Int): Builder {
            val animation = AnimationUtils.loadAnimation(
                    rootViewGroup.context, animationInResRoot)
            return animationShowRoot(animation)
        }

        fun animationHideRoot(animationOutRoot: Animation?): Builder {
            animHideRoot = animationOutRoot
            return this
        }

        fun animationHideRoot(@AnimRes animationOutResRoot: Int): Builder {
            val animation = AnimationUtils.loadAnimation(
                    rootViewGroup.context, animationOutResRoot)
            return animationHideRoot(animation)
        }

        /**
         * View found by provided backgroundViewId should be children of provided rootViewGroup
         */
        fun backgroundView(@IdRes backgroundViewId: Int): Builder {
            val backgroundView = rootViewGroup.findViewById<View>(backgroundViewId)
            return if (backgroundView == null) this else backgroundView(backgroundView, false)
        }

        /**
         * Provided backgroundView should be children of provided rootViewGroup
         */
        fun backgroundView(backgroundView: View): Builder =  backgroundView(backgroundView, true)

        /**
         * @param needCheck Determines should backgroundView validity be checked
         */
        @VisibleForTesting
        internal fun backgroundView(backgroundView: View, needCheck: Boolean): Builder {
            if (needCheck) {
                val isBackgroundSuitable = isChildViewSuitable(backgroundView, rootViewGroup)
                if (isBackgroundSuitable) {
                    this.backgroundView = backgroundView
                    requestSetDefaultAnimForBackground()
                }
            } else {
                this.backgroundView = backgroundView
                requestSetDefaultAnimForBackground()
            }

            return this
        }

        private fun requestSetDefaultAnimForBackground() {
            if (animShowBackground == null) {
                animationShowBackground(R.anim.overlay_module_fade_in)
            }
            if (animHideBackground == null) {
                animationHideBackground(R.anim.overlay_module_fade_out)
            }
        }

        /**
         * View found by provided backgroundViewId should be children of provided rootViewGroup
         */
        fun contentView(@IdRes contentViewId: Int): Builder {
            val contentView = rootViewGroup.findViewById<View>(contentViewId)
            return if (contentView == null) this else contentView(contentView, false)
        }

        /**
         * Provided backgroundView should be children of provided rootViewGroup
         */
        fun contentView(contentView: View): Builder = contentView(contentView, true)

        /**
         * @param needCheck Determines should contentView validity be checked
         */
        @VisibleForTesting
        internal fun contentView(contentView: View, needCheck: Boolean): Builder {
            if (needCheck) {
                val isContentSuitable = isChildViewSuitable(contentView, rootViewGroup)
                if (isContentSuitable) {
                    this.contentView = contentView
                    // set default animations if contentView is valid
                    requestSetDefaultAnimForContent()
                }
            } else {
                this.contentView = contentView
                requestSetDefaultAnimForContent()
            }

            return this
        }

        private fun requestSetDefaultAnimForContent() {
            if (animShowContent == null) {
                animationShowContent(R.anim.overlay_module_slide_up)
            }
            if (animHideContent == null) {
                animationHideContent(R.anim.overlay_module_slide_down)
            }
        }

        fun animationShowBackground(animationInBackground: Animation?): Builder {
            animShowBackground = animationInBackground
            return this
        }

        fun animationShowBackground(@AnimRes animationInResBackground: Int): Builder {
            val animation = AnimationUtils.loadAnimation(
                    rootViewGroup.context, animationInResBackground)
            return animationShowBackground(animation)
        }

        fun animationHideBackground(animationOutBackground: Animation?): Builder {
            animHideBackground = animationOutBackground
            return this
        }

        fun animationHideBackground(@AnimRes animationOutResBackground: Int): Builder {
            val animation = AnimationUtils.loadAnimation(
                    rootViewGroup.context, animationOutResBackground)
            return animationHideBackground(animation)
        }

        fun animationShowContent(animationInContent: Animation?): Builder {
            animShowContent = animationInContent
            return this
        }

        fun animationShowContent(@AnimRes animationInResContent: Int): Builder {
            val animation = AnimationUtils.loadAnimation(
                    rootViewGroup.context, animationInResContent)
            return animationShowContent(animation)
        }

        fun animationHideContent(animationOutContent: Animation?): Builder {
            animHideContent = animationOutContent
            return this
        }

        fun animationHideContent(@AnimRes animationOutResContent: Int): Builder {
            val animation = AnimationUtils.loadAnimation(
                    rootViewGroup.context, animationOutResContent)
            return animationHideContent(animation)
        }

        fun overlayListener(overlayListener: OverlayListener?): Builder {
            this.overlayListener = overlayListener
            return this
        }

        /**
         * @param doHideByClickOnBackground check [Overlay.doHideByClickOnBackground]
         */
        fun setHideByClickOnBackground(doHideByClickOnBackground: Boolean): Builder {
            this.doHideByClickOnBackground = doHideByClickOnBackground
            return this
        }

        fun setHideByBackPressed(doHideByBackPressed: Boolean): Builder {
            this.doHideByBackPressed = doHideByBackPressed
            return this
        }

        /**
         * @param childView Should meet next criteria:
         * 1) not equals to parentViewGroup
         * 2) parentViewGroup contains childView
         */
        @VisibleForTesting
        internal fun isChildViewSuitable(childView: View,
                                         parentViewGroup: ViewGroup): Boolean {
            if (childView != parentViewGroup) {
                val isParentContainChild = contains(childView, parentViewGroup)

                return when {
                    isParentContainChild -> true
                    else -> {
                        log("View should be child in provided ViewGroup")
                        false
                    }
                }
            } else {
                log("Child cannot be a parent simultaneously")
                return false
            }
        }

        @VisibleForTesting
        internal fun contains(view: View, viewGroup: ViewGroup): Boolean {
            val viewParent = view.parent
            return viewParent != null && viewParent === viewGroup
        }

        fun build(): Overlay = Overlay(
                rootViewGroup = rootViewGroup,
                animShowRoot = animShowRoot,
                animHideRoot = animHideRoot,
                backgroundView = backgroundView,
                animShowBackground = animShowBackground,
                animHideBackground = animHideBackground,
                contentView = contentView,
                animShowContent = animShowContent,
                animHideContent = animHideContent,
                overlayListener = overlayListener,
                doHideByClickOnBackground = doHideByClickOnBackground,
                doHideByBackPressed = doHideByBackPressed,
                idForDebug = idForDebug)
    }

    interface OverlayListener {

        fun stateChanged(state: OverlayState)

    }
}
