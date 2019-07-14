package com.android.oleksandrpriadko.overlay

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation

import android.view.animation.Animation.AnimationListener
import androidx.annotation.VisibleForTesting
import com.android.oleksandrpriadko.overlay.HideMethod.DEFAULT

internal class OverlayRenderer(private val holdersContainerViewGroup: ViewGroup,
                               private val rendererListener: RendererListener?) {

    fun display(overlay: Overlay) {
        val rootViewGroup = overlay.rootViewGroup

        val animShowRoot = overlay.animShowRoot
        val animShowBackground = overlay.animShowBackground
        val animShowContent = overlay.animShowContent

        holdersContainerViewGroup.addView(rootViewGroup)

        requestSetUpHideByClickOnBackground(overlay)

        animate(overlay,
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
    fun hide(overlay: Overlay, forceAnimation: Boolean, hideMethod: HideMethod) {
        if (forceAnimation) {
            requestCancelAnimations(overlay, isShowAnimation = false)
        }
        requestCancelAnimations(overlay, isShowAnimation = true)

        val animHideRoot = overlay.animHideRoot
        val animHideBackground = overlay.animHideBackground
        val animHideContent = overlay.animHideContent

        animate(overlay,
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
    private fun animate(overlay: Overlay,
                        animRoot: Animation?,
                        animBackground: Animation?,
                        animContent: Animation?,
                        forceAnimation: Boolean,
                        hideMethod: HideMethod?,
                        isShowAnimation: Boolean) {
        if (animRoot == null && animContent == null && animBackground == null) {
            if (isShowAnimation) {
                notifyOnDisplay(overlay)
            } else {
                notifyOnDismiss(overlay, hideMethod ?: DEFAULT)
            }
        } else {
            startLongestAnimation(
                    overlay,
                    overlay.rootViewGroup,
                    overlay.backgroundView,
                    overlay.contentView,
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
    fun startLongestAnimation(overlay: Overlay,
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
                requestAnimation(overlay, backgroundView, false, animBackground, forceAnimation, isShowAnimation, hideMethod)
                log("content, " + "no notice")
                requestAnimation(overlay, contentView, false, animContent, forceAnimation, isShowAnimation, hideMethod)
                log("root, " + "notice")
                requestAnimation(overlay, rootViewGroup, true, animRoot, forceAnimation, isShowAnimation, hideMethod, isAnimatingRoot = true)
            } else if (isRootLongerThanBack) {
                log("root, " + "no notice")
                requestAnimation(overlay, rootViewGroup, false, animRoot, forceAnimation, isShowAnimation, hideMethod, isAnimatingRoot = true)
                log("back, " + "no notice")
                requestAnimation(overlay, backgroundView, false, animBackground, forceAnimation, isShowAnimation, hideMethod)
                log("content, " + "notice")
                requestAnimation(overlay, contentView, true, animContent, forceAnimation, isShowAnimation, hideMethod)
            } else {
                log("root, " + "no notice")
                requestAnimation(overlay, rootViewGroup, false, animRoot, forceAnimation, isShowAnimation, hideMethod, isAnimatingRoot = true)
                log("content, " + "no notice")
                requestAnimation(overlay, contentView, false, animContent, forceAnimation, isShowAnimation, hideMethod)
                log("back, " + "notice")
                requestAnimation(overlay, backgroundView, true, animBackground, forceAnimation, isShowAnimation, hideMethod)
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
                requestAnimation(overlay, contentView, false, animContent, forceAnimation, isShowAnimation, hideMethod)
                log("back, " + "notice")
                requestAnimation(overlay, backgroundView, true, animBackground, forceAnimation, isShowAnimation, hideMethod)
            } else {
                log("back, " + "no notice")
                requestAnimation(overlay, backgroundView, false, animBackground, forceAnimation, isShowAnimation, hideMethod)
                log("content, " + "notice")
                requestAnimation(overlay, contentView, true, animContent, forceAnimation, isShowAnimation, hideMethod)
            }
        } else if (animContent != null && contentView != null) {
            // only contentAnimation not null
            log("content, " + "notice")
            requestAnimation(overlay, contentView, true, animContent, forceAnimation, isShowAnimation, hideMethod)
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
    fun requestAnimation(overlay: Overlay,
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
                        overlay, isShowAnimation, hideMethod, targetView, isAnimatingRoot)
                animation.setAnimationListener(animationListener)
            }
            if (isShowAnimation) {
                if (targetView != null) {
                    log("anim proceed $overlay")
                    targetView.startAnimation(animation)
                } else {
                    log("cannot proceed anim as targetView is null")
                }
            } else {
                forceOrPlayOutAnimation(
                        overlay, targetView, animation, animationListener, forceAnimation, needNotice)
            }
        }
    }

    private fun createAnimationListener(overlay: Overlay,
                                        isShowAnimation: Boolean,
                                        hideMethod: HideMethod?,
                                        animatingView: View?,
                                        isAnimatingRoot: Boolean): AnimationListener {
        return object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (isShowAnimation) {
                    notifyOnAnimateShow(overlay)
                } else {
                    notifyOnAnimateDismiss(overlay, hideMethod ?: DEFAULT)
                }
            }

            override fun onAnimationEnd(animation: Animation) {
                if (isShowAnimation) {
                    notifyOnDisplay(overlay)
                } else {
                    if (!isAnimatingRoot) {
                        preventFlicker()
                    }
                    notifyOnDismiss(overlay, hideMethod ?: DEFAULT)
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
    fun forceOrPlayOutAnimation(overlay: Overlay,
                                targetView: View?,
                                animation: Animation,
                                animationListener: AnimationListener?,
                                forceAnimation: Boolean,
                                needNotice: Boolean) {
        if (forceAnimation) {
            if (needNotice) {
                if (animationListener != null) {
                    log("anim forced with notice $overlay")
                    animationListener.onAnimationStart(animation)
                    animationListener.onAnimationEnd(animation)
                } else {
                    log("cannot notice as animation listener is null")
                }
            } else {
                log("anim forced $overlay")
            }
        } else {
            if (targetView != null) {
                log("anim proceed $overlay")
                targetView.startAnimation(animation)
            } else {
                log("cannot proceed anim as targetView is null")
            }
        }
    }

    fun removeFromHoldersContainerViewGroup(overlay: Overlay) {
        holdersContainerViewGroup.removeView(overlay.rootViewGroup)
    }

    private fun requestSetUpHideByClickOnBackground(overlay: Overlay) {
        if (overlay.doHideByClickOnBackground && overlay.backgroundView != null) {
            overlay.backgroundView.setOnClickListener { notifyOnTouchedBackground(overlay) }
        } else if (overlay.backgroundView == null) {
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

    fun requestCancelAnimations(overlay: Overlay,
                                isShowAnimation: Boolean) {
        if (isShowAnimation) {
            requestCancelAnim(overlay.animShowRoot)
            requestCancelAnim(overlay.animShowBackground)
            requestCancelAnim(overlay.animShowContent)
        } else {
            requestCancelAnim(overlay.animHideRoot)
            requestCancelAnim(overlay.animHideBackground)
            requestCancelAnim(overlay.animHideContent)
        }
    }

    private fun requestCancelAnim(animation: Animation?) {
        if (animation != null && animation.hasStarted()) {
            animation.setAnimationListener(null)
            animation.cancel()
        }
    }

    private fun notifyOnAnimateShow(overlay: Overlay)
            = rendererListener?.onAnimateShow(overlay)

    private fun notifyOnDisplay(overlay: Overlay)
            = rendererListener?.onDisplay(overlay)

    private fun notifyOnAnimateDismiss(overlay: Overlay, hideMethod: HideMethod)
            = rendererListener?.onAnimateDismiss(overlay, hideMethod)

    private fun notifyOnDismiss(overlay: Overlay, hideMethod: HideMethod)
            = rendererListener?.onDismiss(overlay, hideMethod)

    private fun notifyOnTouchedBackground(overlay: Overlay)
            = rendererListener?.onBackgroundClicked(overlay)

    private fun getAnimationDuration(animation: Animation?): Long
            = if (animation == null) 0 else animation.duration + animation.startOffset

    private fun log(message: String)
            = OverlayManager.log("${OverlayRenderer::class.java.simpleName} $message")

    internal interface RendererListener {

        fun onAnimateShow(overlay: Overlay)

        fun onDisplay(overlay: Overlay)

        fun onAnimateDismiss(overlay: Overlay, hideMethod: HideMethod)

        fun onDismiss(overlay: Overlay, hideMethod: HideMethod)

        fun onBackgroundClicked(overlay: Overlay)

    }
}
