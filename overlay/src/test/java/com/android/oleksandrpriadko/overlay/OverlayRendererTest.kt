package com.android.oleksandrpriadko.overlay

import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import com.android.oleksandrpriadko.overlay.HideMethod.CLICK_ON_BACKGROUND
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OverlayRendererTest {

    private lateinit var overlayRenderer: OverlayRenderer
    @Mock
    private lateinit var holdersRoot: ViewGroup

    @Mock
    private lateinit var root: ViewGroup
    @Mock
    private lateinit var back: ViewGroup
    @Mock
    private lateinit var content: ViewGroup

    @Mock
    private lateinit var rootInAnim: Animation
    @Mock
    private lateinit var backInAnim: Animation
    @Mock
    private lateinit var contentInAnim: Animation

    @Mock
    private lateinit var rootOutAnim: Animation
    @Mock
    private lateinit var backOutAnim: Animation
    @Mock
    private lateinit var contentOutAnim: Animation

    @Mock
    private lateinit var animationListener: AnimationListener

    @Before
    @Throws(Exception::class)
    fun setUp() {
        overlayRenderer = OverlayRenderer(holdersRoot, null)
    }

    @Test
    fun givenRootLongest_whenStartLongestAnimation_onlyRootRasListener() {
        `when`(rootInAnim.duration).thenReturn(1000L)
        `when`(backInAnim.duration).thenReturn(100L)
        `when`(contentInAnim.duration).thenReturn(10L)

        overlayRenderer.startLongestAnimation(
                createOverlayHolder(),
                root,
                back,
                content,
                rootInAnim,
                backInAnim,
                contentInAnim,
                forceAnimation = false,
                isShowAnimation = false,
                hideMethod = CLICK_ON_BACKGROUND
        )

        verify(rootInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(backInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(contentInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))

        verify(root).startAnimation(rootInAnim)
        verify<ViewGroup>(back).startAnimation(backInAnim)
        verify<ViewGroup>(content).startAnimation(contentInAnim)
    }

    @Test
    fun givenBackLongest_whenStartLongestAnimation_onlyBackHasListener() {
        `when`(rootInAnim.duration).thenReturn(100L)
        `when`(backInAnim.duration).thenReturn(1000L)
        `when`(contentInAnim.duration).thenReturn(10L)

        overlayRenderer.startLongestAnimation(
                createOverlayHolder(),
                root,
                back,
                content,
                rootInAnim,
                backInAnim,
                contentInAnim,
                forceAnimation = false,
                isShowAnimation = false,
                hideMethod = null
        )

        verify(rootInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(backInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(contentInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))

        verify(root).startAnimation(rootInAnim)
        verify<ViewGroup>(back).startAnimation(backInAnim)
        verify<ViewGroup>(content).startAnimation(contentInAnim)
    }

    @Test
    fun givenContentLongest_whenStartLongestAnimation_onlyContentHasListener() {
        `when`(rootInAnim.duration).thenReturn(100L)
        `when`(backInAnim.duration).thenReturn(10L)
        `when`(contentInAnim.duration).thenReturn(1000L)

        overlayRenderer.startLongestAnimation(
                createOverlayHolder(),
                root,
                back,
                content,
                rootInAnim,
                backInAnim,
                contentInAnim,
                forceAnimation = false,
                isShowAnimation = false,
                hideMethod = CLICK_ON_BACKGROUND
        )

        verify(rootInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(backInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(contentInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))

        verify(root).startAnimation(rootInAnim)
        verify<ViewGroup>(back).startAnimation(backInAnim)
        verify<ViewGroup>(content).startAnimation(contentInAnim)
    }

    @Test
    fun givenRootNullContentLongest_whenStartLongestAnimation_onlyContentHasListener() {
        `when`(backInAnim.duration).thenReturn(10L)
        `when`(contentInAnim.duration).thenReturn(1000L)

        overlayRenderer.startLongestAnimation(
                createOverlayHolder(),
                root,
                back,
                content, null,
                backInAnim,
                contentInAnim,
                forceAnimation = false,
                isShowAnimation = false,
                hideMethod = null
        )

        verify<Animation>(rootInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(backInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(contentInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))

        verify(root, never()).startAnimation(rootInAnim)
        verify<ViewGroup>(back).startAnimation(backInAnim)
        verify<ViewGroup>(content).startAnimation(contentInAnim)
    }

    @Test
    fun givenRootNullBackLongest_whenStartLongestAnimation_onlyBackHasListener() {
        `when`(backInAnim.duration).thenReturn(1000L)
        `when`(contentInAnim.duration).thenReturn(10L)

        overlayRenderer.startLongestAnimation(
                createOverlayHolder(),
                root,
                back,
                content, null,
                backInAnim,
                contentInAnim,
                forceAnimation = false,
                isShowAnimation = false,
                hideMethod = null
        )

        verify<Animation>(rootInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(backInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(contentInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))

        verify(root, never()).startAnimation(rootInAnim)
        verify<ViewGroup>(back).startAnimation(backInAnim)
        verify<ViewGroup>(content).startAnimation(contentInAnim)
    }

    @Test
    fun givenRootNullBackNull_whenStartLongestAnimation_onlyContentHasListener() {
        `when`(contentInAnim.duration).thenReturn(10L)

        overlayRenderer.startLongestAnimation(
                createOverlayHolder(),
                root,
                back,
                content, null, null,
                contentInAnim,
                forceAnimation = false,
                isShowAnimation = false,
                hideMethod = CLICK_ON_BACKGROUND
        )

        verify<Animation>(rootInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify<Animation>(backInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify(contentInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))

        verify(root, never()).startAnimation(rootInAnim)
        verify<ViewGroup>(back, never()).startAnimation(backInAnim)
        verify<ViewGroup>(content).startAnimation(contentInAnim)
    }

    @Test
    fun givenAnimNull_whenRequestAnimation_doNothing() {
        overlayRenderer.requestAnimation(
                createOverlayHolder(), root, true, null, forceAnimation = false, isShowAnimation = false, hideMethod = null)

        verify<Animation>(rootInAnim, never()).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
    }

    @Test
    fun givenAnimValidNeedNotice_whenRequestAnimation_setListenerStart() {
        overlayRenderer.requestAnimation(
                createOverlayHolder(), root, true, rootInAnim, forceAnimation = false, isShowAnimation = false, hideMethod = CLICK_ON_BACKGROUND)

        verify<Animation>(rootInAnim).setAnimationListener(any<AnimationListener>(AnimationListener::class.java))
        verify<ViewGroup>(root).startAnimation(rootInAnim)
    }

    @Test
    fun givenForcedNoNotice_whenForceAnimationIfNeed_doNothing() {
        overlayRenderer.forceOrPlayOutAnimation(
                createOverlayHolder(), root, rootInAnim, animationListener, forceAnimation = true, needNotice = false)

        verify<AnimationListener>(animationListener, never()).onAnimationStart(rootInAnim)
        verify<AnimationListener>(animationListener, never()).onAnimationEnd(rootInAnim)

        verify<ViewGroup>(root, never()).startAnimation(rootInAnim)
    }

    @Test
    fun givenForcedNotice_whenForceAnimationIfNeed_AnimListenerTriggered() {
        overlayRenderer.forceOrPlayOutAnimation(
                createOverlayHolder(), root, rootInAnim, animationListener, forceAnimation = true, needNotice = true)

        verify<AnimationListener>(animationListener).onAnimationStart(rootInAnim)
        verify<AnimationListener>(animationListener).onAnimationEnd(rootInAnim)

        verify<ViewGroup>(root, never()).startAnimation(rootInAnim)
    }

    @Test
    fun givenNoForcedNotice_whenForceAnimationIfNeed_startAnim() {
        overlayRenderer.forceOrPlayOutAnimation(
                createOverlayHolder(), root, rootInAnim, animationListener, forceAnimation = false, needNotice = true)

        verify<AnimationListener>(animationListener, never()).onAnimationStart(rootInAnim)
        verify<AnimationListener>(animationListener, never()).onAnimationEnd(rootInAnim)

        verify<ViewGroup>(root).startAnimation(rootInAnim)
    }

    @Test
    fun givenInAnim_whenRequestCancelAnimations_cancelInOnly() {
        `when`(rootInAnim.hasStarted()).thenReturn(true)
        `when`(backInAnim.hasStarted()).thenReturn(true)
        `when`(contentInAnim.hasStarted()).thenReturn(true)

        val overlayHolder = createOverlayHolder()

        overlayRenderer.requestCancelAnimations(overlayHolder, true)

        verify(rootInAnim).setAnimationListener(null)
        verify(rootInAnim).cancel()
        verify(backInAnim).setAnimationListener(null)
        verify(backInAnim).cancel()
        verify(contentInAnim).setAnimationListener(null)
        verify(contentInAnim).cancel()

        verify<Animation>(rootOutAnim, never()).setAnimationListener(null)
        verify<Animation>(rootOutAnim, never()).cancel()
        verify<Animation>(backOutAnim, never()).setAnimationListener(null)
        verify<Animation>(backOutAnim, never()).cancel()
        verify<Animation>(contentOutAnim, never()).setAnimationListener(null)
        verify<Animation>(contentOutAnim, never()).cancel()
    }

    @Test
    fun givenOutAnim_whenRequestCancelAnimations_cancelOutOnly() {
        `when`(rootOutAnim.hasStarted()).thenReturn(true)
        `when`(backOutAnim.hasStarted()).thenReturn(true)
        `when`(contentOutAnim.hasStarted()).thenReturn(true)

        val overlayHolder = createOverlayHolder()

        overlayRenderer.requestCancelAnimations(overlayHolder, false)

        verify(rootOutAnim).setAnimationListener(null)
        verify(rootOutAnim).cancel()
        verify(backOutAnim).setAnimationListener(null)
        verify(backOutAnim).cancel()
        verify(contentOutAnim).setAnimationListener(null)
        verify(contentOutAnim).cancel()

        verify<Animation>(rootInAnim, never()).setAnimationListener(null)
        verify<Animation>(rootInAnim, never()).cancel()
        verify<Animation>(backInAnim, never()).setAnimationListener(null)
        verify<Animation>(backInAnim, never()).cancel()
        verify<Animation>(contentInAnim, never()).setAnimationListener(null)
        verify<Animation>(contentInAnim, never()).cancel()
    }

    private fun createOverlayHolder(): Overlay {
        val overlayHolderBuilder = Overlay.Builder(root)

        overlayHolderBuilder.backgroundView(back)
        overlayHolderBuilder.contentView(content)

        overlayHolderBuilder.animationShowRoot(rootInAnim)
        overlayHolderBuilder.animationHideRoot(rootOutAnim)

        overlayHolderBuilder.animationShowBackground(backInAnim)
        overlayHolderBuilder.animationHideBackground(backOutAnim)

        overlayHolderBuilder.animationShowContent(contentInAnim)
        overlayHolderBuilder.animationHideContent(contentOutAnim)

        return overlayHolderBuilder.build()
    }
}