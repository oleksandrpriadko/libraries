package com.android.oleksandrpriadko.overlay

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation

import android.view.animation.Animation.AnimationListener
import androidx.annotation.VisibleForTesting
import com.android.oleksandrpriadko.overlay.HideMethod.DEFAULT

internal class OverlayRenderer(private val holdersContainerViewGroup: ViewGroup,
                               private val rendererListener: RendererListener?) {

    fun display(overlayHolder: Overlay) {
        val rootViewGroup = overlayHolder.rootViewGroup

        val animShowRoot = overlayHolder.animShowRoot
        val animShowBackground = overlayHolder.animShowBackground
        val animShowContent = overlayHolder.animShowContent

        notifyOnDisplayInProgress(overlayHolder)

        holdersContainerViewGroup.addView(rootViewGroup)

        requestSetUpHideByClickOnBackground(overlayHolder)

        animate(overlayHolder,
                animShowRoot,
                animShowBackground,
                animShowContent,
                forceAnimation = false,
                hideMethod = null,
                isShowAnimation = true)
    }

    /**
     * @param forceAnimation If true - animation will not be played, false - otherwise
     */
    fun hide(overlayHolder: Overlay, forceAnimation: Boolean, hideMethod: HideMethod) {
        if (forceAnimation) {
            requestCancelAnimations(overlayHolder, isShowAnimation = false)
        }
        requestCancelAnimations(overlayHolder, isShowAnimation = true)

        val animHideRoot = overlayHolder.animHideRoot
        val animHideBackground = overlayHolder.animHideBackground
        val animHideContent = overlayHolder.animHideContent

        notifyOnDismissInProgress(overlayHolder, hideMethod)

        animate(overlayHolder,
                animHideRoot,
                animHideBackground,
                animHideContent,
                forceAnimation,
                hideMethod,
                isShowAnimation = false)
    }

    /**
     * @param forceAnimation check [.hide]
     */
    private fun animate(overlayHolder: Overlay,
                        animRoot: Animation?,
                        animBackground: Animation?,
                        animContent: Animation?,
                        forceAnimation: Boolean,
                        hideMethod: HideMethod?,
                        isShowAnimation: Boolean) {
        if (animRoot == null && animContent == null && animBackground == null) {
            if (isShowAnimation) {
                notifyOnDisplay(overlayHolder)
            } else {
                notifyOnDismiss(overlayHolder, hideMethod ?: DEFAULT)
            }
        } else {
            startLongestAnimation(
                    overlayHolder,
                    overlayHolder.rootViewGroup,
                    overlayHolder.backgroundView,
                    overlayHolder.contentView,
                    animRoot,
                    animBackground,
                    animContent,
                    forceAnimation,
                    isShowAnimation,
                    hideMethod)
        }
    }

    /**
     * Play root, background and content animations if valid. Longest of them will notify
     * [.rendererListener] that animation finished
     *
     * @param forceAnimation check [.hide]
     */
    @VisibleForTesting
    fun startLongestAnimation(overlayHolder: Overlay,
                              rootViewGroup: ViewGroup,
                              backgroundView: View?,
                              contentView: View?,
                              animRoot: Animation?,
                              animBackground: Animation?,
                              animContent: Animation?,
                              forceAnimation: Boolean,
                              isShowAnimation: Boolean,
                              hideMethod: HideMethod?) {
        if (animRoot != null) {
            // compare root, back and content animations
            val durationRoot = getAnimationDuration(animRoot)
            var isRootLongerThanBack = true
            if (animBackground != null && backgroundView != null) {
                val durationBack = getAnimationDuration(animBackground)
                isRootLongerThanBack = durationRoot >= durationBack
            }
            var isRootLongerThanContent = true
            if (animContent != null && contentView != null) {
                val durationContent = getAnimationDuration(animContent)
                isRootLongerThanContent = durationRoot >= durationContent
            }
            if (isRootLongerThanBack && isRootLongerThanContent) {
                log("back, " + "no notice")
                requestAnimation(overlayHolder, backgroundView, false, animBackground, forceAnimation, isShowAnimation, hideMethod)
                log("content, " + "no notice")
                requestAnimation(overlayHolder, contentView, false, animContent, forceAnimation, isShowAnimation, hideMethod)
                log("root, " + "notice")
                requestAnimation(overlayHolder, rootViewGroup, true, animRoot, forceAnimation, isShowAnimation, hideMethod, isAnimatingRoot = true)
            } else if (isRootLongerThanBack) {
                log("root, " + "no notice")
                requestAnimation(overlayHolder, rootViewGroup, false, animRoot, forceAnimation, isShowAnimation, hideMethod, isAnimatingRoot = true)
                log("back, " + "no notice")
                requestAnimation(overlayHolder, backgroundView, false, animBackground, forceAnimation, isShowAnimation, hideMethod)
                log("content, " + "notice")
                requestAnimation(overlayHolder, contentView, true, animContent, forceAnimation, isShowAnimation, hideMethod)
            } else {
                log("root, " + "no notice")
                requestAnimation(overlayHolder, rootViewGroup, false, animRoot, forceAnimation, isShowAnimation, hideMethod, isAnimatingRoot = true)
                log("content, " + "no notice")
                requestAnimation(overlayHolder, contentView, false, animContent, forceAnimation, isShowAnimation, hideMethod)
                log("back, " + "notice")
                requestAnimation(overlayHolder, backgroundView, true, animBackground, forceAnimation, isShowAnimation, hideMethod)
            }
        } else if (animBackground != null && backgroundView != null) {
            // compare back and content animations
            val durationBack = getAnimationDuration(animBackground)
            var isBackLongerThanContent = true
            if (animContent != null && contentView != null) {
                val durationContent = getAnimationDuration(animContent)
                isBackLongerThanContent = durationBack >= durationContent
            }
            if (isBackLongerThanContent) {
                log("content, " + "no notice")
                requestAnimation(overlayHolder, contentView, false, animContent, forceAnimation, isShowAnimation, hideMethod)
                log("back, " + "notice")
                requestAnimation(overlayHolder, backgroundView, true, animBackground, forceAnimation, isShowAnimation, hideMethod)
            } else {
                log("back, " + "no notice")
                requestAnimation(overlayHolder, backgroundView, false, animBackground, forceAnimation, isShowAnimation, hideMethod)
                log("content, " + "notice")
                requestAnimation(overlayHolder, contentView, true, animContent, forceAnimation, isShowAnimation, hideMethod)
            }
        } else if (animContent != null && contentView != null) {
            // only contentAnimation not null
            log("content, " + "notice")
            requestAnimation(overlayHolder, contentView, true, animContent, forceAnimation, isShowAnimation, hideMethod)
        } else {
            log("cannot play animation on any of views")
        }
    }

    /**
     * @param needNotice     If true - [AnimationListener]
     * will be set to animation and [.rendererListener] will
     * be notified once
     * [AnimationListener.onAnimationEnd]
     * is triggered
     * @param forceAnimation If true - force OUTER animation, false - let it play
     */
    @VisibleForTesting
    fun requestAnimation(overlayHolder: Overlay,
                         targetView: View?,
                         needNotice: Boolean,
                         animation: Animation?,
                         forceAnimation: Boolean,
                         isShowAnimation: Boolean,
                         hideMethod: HideMethod?,
                         isAnimatingRoot: Boolean = false) {
        if (animation != null) {
            var animationListener: AnimationListener? = null
            if (needNotice) {
                animationListener = createAnimationListener(
                        overlayHolder, isShowAnimation, hideMethod, targetView, isAnimatingRoot)
                animation.setAnimationListener(animationListener)
            }
            if (isShowAnimation) {
                if (targetView != null) {
                    log("anim proceed $overlayHolder")
                    targetView.startAnimation(animation)
                } else {
                    log("cannot proceed anim as targetView is null")
                }
            } else {
                forceOrPlayOutAnimation(
                        overlayHolder, targetView, animation, animationListener, forceAnimation, needNotice)
            }
        }
    }

    private fun createAnimationListener(overlayHolder: Overlay,
                                        isShowAnimation: Boolean,
                                        hideMethod: HideMethod?,
                                        animatingView: View?,
                                        isAnimatingRoot: Boolean): AnimationListener {
        return object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                if (isShowAnimation) {
                    notifyOnDisplay(overlayHolder)
                } else {
                    if (!isAnimatingRoot) {
                        preventFlicker()
                    }
                    notifyOnDismiss(overlayHolder, hideMethod ?: DEFAULT)
                }
            }

            /**
             * There is one small limitation:
             * If you want to show more than one overlay and play animation on background or content and they
             * are children exactly of rootView - you might experience flicker.
             * To prevent it we hide parent by set alpha to 0f and then show parent
             * by original value of alpha
             */
            private fun preventFlicker() {
                if (animatingView != null) {
                    val viewGroup = animatingView.parent as ViewGroup
                    val initialParentAlpha = viewGroup.alpha
                    // hide parent
                    viewGroup.alpha = 0f
                    // restore parents alpha
                    viewGroup.post { viewGroup.alpha = initialParentAlpha }
                } else {
                    log("cannot prevent flicker as animating view is null")
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        }
    }

    /**
     * If forceAnimation is true -
     *
     * @param forceAnimation check [.requestAnimation]
     * @param needNotice     check [.requestAnimation]
     */
    @VisibleForTesting
    fun forceOrPlayOutAnimation(overlayHolder: Overlay,
                                targetView: View?,
                                animation: Animation,
                                animationListener: AnimationListener?,
                                forceAnimation: Boolean,
                                needNotice: Boolean) {
        if (forceAnimation) {
            if (needNotice) {
                if (animationListener != null) {
                    log("anim forced with notice $overlayHolder")
                    animationListener.onAnimationStart(animation)
                    animationListener.onAnimationEnd(animation)
                } else {
                    log("cannot notice as animation listener is null")
                }
            } else {
                log("anim forced $overlayHolder")
            }
        } else {
            if (targetView != null) {
                log("anim proceed $overlayHolder")
                targetView.startAnimation(animation)
            } else {
                log("cannot proceed anim as targetView is null")
            }
        }
    }

    fun removeFromHoldersContainerViewGroup(overlayHolder: Overlay) {
        holdersContainerViewGroup.removeView(overlayHolder.rootViewGroup)
    }

    private fun requestSetUpHideByClickOnBackground(overlayHolder: Overlay) {
        if (overlayHolder.doHideByClickOnBackground && overlayHolder.backgroundView != null) {
            overlayHolder.backgroundView.setOnClickListener { notifyOnTouchedBackground(overlayHolder) }
        } else if (overlayHolder.backgroundView == null) {
            log("cannot be dismissed by click on background as background view is not provided or null")
        }
    }

    fun showHoldersContainerViewGroup(show: Boolean) {
        if (show && holdersContainerViewGroup.visibility != View.VISIBLE) {
            holdersContainerViewGroup.visibility = View.VISIBLE
        } else if (!show && holdersContainerViewGroup.visibility != View.GONE) {
            holdersContainerViewGroup.visibility = View.GONE
        }
    }

    fun requestCancelAnimations(overlayHolder: Overlay,
                                isShowAnimation: Boolean) {
        if (isShowAnimation) {
            requestCancelAnim(overlayHolder.animShowRoot)
            requestCancelAnim(overlayHolder.animShowBackground)
            requestCancelAnim(overlayHolder.animShowContent)
        } else {
            requestCancelAnim(overlayHolder.animHideRoot)
            requestCancelAnim(overlayHolder.animHideBackground)
            requestCancelAnim(overlayHolder.animHideContent)
        }
    }

    private fun requestCancelAnim(animation: Animation?) {
        if (animation != null && animation.hasStarted()) {
            animation.setAnimationListener(null)
            animation.cancel()
        }
    }

    private fun notifyOnDisplayInProgress(overlayHolder: Overlay)
            = rendererListener?.onDisplayInProgress(overlayHolder)

    private fun notifyOnDisplay(overlayHolder: Overlay)
            = rendererListener?.onDisplay(overlayHolder)

    private fun notifyOnDismissInProgress(overlayHolder: Overlay, hideMethod: HideMethod)
            = rendererListener?.onDismissInProgress(overlayHolder, hideMethod)

    private fun notifyOnDismiss(overlayHolder: Overlay, hideMethod: HideMethod)
            = rendererListener?.onDismiss(overlayHolder, hideMethod)

    private fun notifyOnTouchedBackground(overlayHolder: Overlay)
            = rendererListener?.onBackgroundClicked(overlayHolder)

    private fun getAnimationDuration(animation: Animation?): Long
            = if (animation == null) 0 else animation.duration + animation.startOffset

    private fun log(message: String)
            = OverlayManager.log("${OverlayRenderer::class.java.simpleName} $message")

    internal interface RendererListener {

        fun onDisplayInProgress(overlay: Overlay)

        fun onDisplay(overlay: Overlay)

        fun onDismissInProgress(overlay: Overlay, hideMethod: HideMethod)

        fun onDismiss(overlay: Overlay, hideMethod: HideMethod)

        fun onBackgroundClicked(overlay: Overlay)

    }
}
