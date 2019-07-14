package com.android.oleksandrpriadko.overlay

import android.os.Handler
import android.view.ViewGroup
import com.android.oleksandrpriadko.overlay.HideMethod.CLICK_ON_BACKGROUND
import com.android.oleksandrpriadko.overlay.HideMethod.DEFAULT
import com.android.oleksandrpriadko.overlay.OverlayState.ANIMATING_IN
import com.android.oleksandrpriadko.overlay.OverlayState.ANIMATING_OUT
import com.android.oleksandrpriadko.overlay.OverlayState.ANIMATING_OUT_BACK_CLICK
import com.android.oleksandrpriadko.overlay.OverlayState.DISMISSED
import com.android.oleksandrpriadko.overlay.OverlayState.DISMISSED_BACK_CLICK
import com.android.oleksandrpriadko.overlay.OverlayState.DISPLAYING
import com.android.oleksandrpriadko.overlay.OverlayState.IDLE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class OverlayManagerTest {

    private lateinit var overlayManager: OverlayManager
    @Mock
    private lateinit var mHandlerMock: Handler
    @Mock
    private lateinit var root: ViewGroup
    @Mock
    private lateinit var mViewGroupRoot: ViewGroup

    @Before
    fun setUp() {
        overlayManager = OverlayManager(root)
    }

    private fun createOverlayHolder(): Overlay {
        return Overlay.Builder(mViewGroupRoot).build()
    }

    @Test
    fun givenStateIdleSameHolder_whenCommit_shouldAddFirst() {
        val overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(1, overlayManager.forTestOverlayList.size)

        overlayManager.add(overlayHolder)
        assertEquals(1, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenStateNotIdleDiffHolders_whenCommit_noAddRemove() {
        var overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_IN)
        overlayManager.add(overlayHolder)
        assertEquals(0, overlayManager.forTestOverlayList.size)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_OUT)
        overlayManager.add(overlayHolder)
        assertEquals(0, overlayManager.forTestOverlayList.size)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISPLAYING)
        overlayManager.add(overlayHolder)
        assertEquals(0, overlayManager.forTestOverlayList.size)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISMISSED)
        overlayManager.add(overlayHolder)
        assertEquals(0, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenStateIdleDiffHolder_whenCommit_shouldAddRemove() {
        var overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(1, overlayManager.forTestOverlayList.size)

        overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(2, overlayManager.forTestOverlayList.size)

        overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(3, overlayManager.forTestOverlayList.size)

        overlayManager.hideLast(DEFAULT)
        assertEquals(2, overlayManager.forTestOverlayList.size)

        overlayManager.hideLast(CLICK_ON_BACKGROUND)
        assertEquals(1, overlayManager.forTestOverlayList.size)

        overlayManager.hideLast(DEFAULT)
        assertEquals(0, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenFirstDisplaying_whenAttach_shouldDisplaySecond() {
        var overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(DISPLAYING, overlayManager.forTestOverlayList[0].state)

        overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(DISPLAYING, overlayManager.forTestOverlayList[1].state)
    }

    @Test
    fun givenFirstAnimIn_whenAttach_shouldSecondStayIdle() {
        var overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        overlayHolder.updateState(ANIMATING_IN)

        overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(IDLE, overlayManager.forTestOverlayList[1].state)
    }

    @Test
    fun givenFirstAnimOut_whenAttach_shouldSecondStayIdle() {
        var overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        overlayHolder.updateState(ANIMATING_OUT)

        overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)
        assertEquals(IDLE, overlayManager.forTestOverlayList[1].state)
    }

    @Test
    fun whenToDisplay() {
        val overlayHolder1 = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolder1)
        val overlayHolder2 = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolder2)

        overlayManager.toDisplay(overlayHolder1, true)

        assertEquals(DISPLAYING, overlayHolder1.state)
        assertEquals(DISPLAYING, overlayHolder2.state)
    }

    @Test
    fun whenStateDismissAndRemoveFromQueueAndViewGroup() {
        var overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)

        overlayManager.stateDismissAndRemoveFromListAndViewGroup(overlayHolder, DEFAULT)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(DISMISSED, overlayHolder.state)

        overlayHolder = createOverlayHolder()
        overlayManager.add(overlayHolder)

        overlayManager.stateDismissAndRemoveFromListAndViewGroup(overlayHolder, CLICK_ON_BACKGROUND)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(DISMISSED_BACK_CLICK, overlayHolder.state)
    }

    @Test
    fun givenEmptyList_whenRequestHideParentAndUnlock_shouldUnlock() {
        overlayManager.setIsDismissingAll(true)

        overlayManager.unlock()

        assertFalse(overlayManager.isHidingAll)
    }

    @Test
    fun givenList_whenRequestHideParentAndUnlock_shouldNotUnlock() {
        overlayManager.add(createOverlayHolder())
        overlayManager.setIsDismissingAll(true)

        overlayManager.unlock()

        assertTrue(overlayManager.isHidingAll)
    }

    @Test
    fun whenRequestDisplayNext() {
        val overlayHolder1 = createOverlayHolder()
        overlayManager.add(overlayHolder1)

        val overlayHolder2 = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolder2)

        assertEquals(overlayHolder1.state, DISPLAYING)
        assertEquals(overlayHolder2.state, IDLE)

        overlayManager.requestDisplayNext(overlayHolder1)

        assertEquals(overlayHolder2.state, DISPLAYING)
    }

    @Test
    fun givenNoHolders_whenPop_returnFalse() {
        assertFalse(overlayManager.hideLast(DEFAULT))
    }

    @Test
    fun givenOneHolder_whenPop_returnFalse() {
        overlayManager.forTestOverlayList.add(createOverlayHolder())
        assertFalse(overlayManager.hideLast(CLICK_ON_BACKGROUND))
    }

    @Test
    fun givenTwoHolder_whenPop_returnFalseOnSecond() {
        overlayManager.forTestOverlayList.add(createOverlayHolder())
        overlayManager.forTestOverlayList.add(createOverlayHolder())

        assertTrue(overlayManager.hideLast(DEFAULT))
        assertFalse(overlayManager.hideLast(CLICK_ON_BACKGROUND))
    }

    @Test
    fun givenHolderStateIdle_whenPop_shouldRemoveStateIdle() {
        val overlayHolder = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(CLICK_ON_BACKGROUND)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(IDLE, overlayHolder.state)
    }

    @Test
    fun givenHolderStateAnimIn_whenPop_shouldRemoveStateDismissed() {
        var overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_IN)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(CLICK_ON_BACKGROUND)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(DISMISSED_BACK_CLICK, overlayHolder.state)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_IN)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(DEFAULT)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(DISMISSED, overlayHolder.state)
    }

    @Test
    fun givenHolderStateDisplaying_whenPop_shouldRemoveStateDismissed() {
        var overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(CLICK_ON_BACKGROUND)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(DISMISSED_BACK_CLICK, overlayHolder.state)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(DEFAULT)

        assertEquals(0, overlayManager.forTestOverlayList.size)
        assertEquals(DISMISSED, overlayHolder.state)
    }

    @Test
    fun givenHolderStateAnimOut_whenPop_doNothing() {
        val overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_OUT)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(DEFAULT)

        assertEquals(1, overlayManager.forTestOverlayList.size)
        assertEquals(ANIMATING_OUT, overlayHolder.state)
    }

    @Test
    fun givenHolderStateAnimOutBackClick_whenPop_doNothing() {
        val overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_OUT_BACK_CLICK)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.hideLast(CLICK_ON_BACKGROUND)

        assertEquals(1, overlayManager.forTestOverlayList.size)
        assertEquals(ANIMATING_OUT_BACK_CLICK, overlayHolder.state)
    }

    @Test
    fun givenStateAnimIn_whenSuspend_shouldStateDisplaying() {
        val overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_IN)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.stop()

        assertEquals(1, overlayManager.forTestOverlayList.size)
        assertEquals(DISPLAYING, overlayHolder.state)
    }

    @Test
    fun givenStateIdle_whenSuspend_doNothing() {
        val overlayHolder = createOverlayHolder()
        overlayHolder.updateState(IDLE)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.stop()

        assertEquals(1, overlayManager.forTestOverlayList.size)
        assertEquals(IDLE, overlayHolder.state)
    }

    @Test
    fun givenStateDisplaying_whenSuspend_doNothing() {
        val overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayManager.stop()

        assertEquals(1, overlayManager.forTestOverlayList.size)
        assertEquals(DISPLAYING, overlayHolder.state)
    }

    @Test
    fun givenFirstAnimIn_whenFindFirstActive_returnAnimIn() {
        var overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_IN)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_OUT)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISMISSED)
        overlayManager.forTestOverlayList.add(overlayHolder)

        val found = overlayManager.findFirstWithState(ANIMATING_IN)

        assertNotNull(found)
        assertEquals(ANIMATING_IN, found!!.state)
    }

    @Test
    fun whenFindReadyToDisplay() {
        var overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_OUT)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(ANIMATING_IN)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(IDLE)
        overlayManager.forTestOverlayList.add(overlayHolder)

        overlayHolder = createOverlayHolder()
        overlayHolder.updateState(DISMISSED)
        overlayManager.forTestOverlayList.add(overlayHolder)

        var found = overlayManager.findReadyToDisplay()

        assertNotNull(found)
        assertEquals(IDLE, found!!.state)

        `when`(overlayHolder.rootViewGroup.parent).thenReturn(mViewGroupRoot)

        found = overlayManager.findReadyToDisplay()

        assertNull(found)
    }

    @Test
    fun givenAllIdle_whenResume_allDisplaying() {
        val overlayHolderFirst = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overHolderSecond = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overHolderSecond)

        overlayManager.resume()

        assertEquals(DISPLAYING, overlayHolderFirst.state)
        assertEquals(DISPLAYING, overHolderSecond.state)
    }

    @Test
    fun givenFirstAnimInNextIdle_whenResume_doNothing() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(ANIMATING_IN)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolderSecond)

        overlayManager.resume()

        assertEquals(ANIMATING_IN, overlayHolderFirst.state)
        assertEquals(IDLE, overlayHolderSecond.state)
    }

    @Test
    fun givenFirstAnimOutNextIdle_whenResume_doNothing() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(ANIMATING_OUT)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolderSecond)

        overlayManager.resume()

        assertEquals(ANIMATING_OUT, overlayHolderFirst.state)
        assertEquals(IDLE, overlayHolderSecond.state)
    }

    @Test
    fun givenFirstDisplayingNextIdle_whenResume_displaySecond() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolderSecond)

        overlayManager.resume()

        assertEquals(DISPLAYING, overlayHolderFirst.state)
        assertEquals(DISPLAYING, overlayHolderSecond.state)
    }

    @Test
    fun givenFirstDismissedNextIdle_whenResume_displaySecond() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(DISMISSED)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolderSecond)

        overlayManager.resume()

        assertEquals(DISMISSED, overlayHolderFirst.state)
        assertEquals(DISPLAYING, overlayHolderSecond.state)
    }

    @Test
    fun givenFirstDismissedBackClickNextIdle_whenResume_displaySecond() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(DISMISSED_BACK_CLICK)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayManager.forTestOverlayList.add(overlayHolderSecond)

        overlayManager.resume()

        assertEquals(DISMISSED_BACK_CLICK, overlayHolderFirst.state)
        assertEquals(DISPLAYING, overlayHolderSecond.state)
    }

    @Test
    fun givenFirstDisplayingNextNotIdle_whenResume_doNothing() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayHolderSecond.updateState(ANIMATING_OUT)
        overlayManager.forTestOverlayList.add(overlayHolderSecond)

        overlayManager.resume()

        assertEquals(DISPLAYING, overlayHolderFirst.state)
        assertEquals(ANIMATING_OUT, overlayHolderSecond.state)
    }

    @Test
    fun givenFirstDisplayingNextIdleHasParent_whenResume_doNothing() {
        val overlayHolderFirst = createOverlayHolder()
        overlayHolderFirst.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolderFirst)
        val overlayHolderSecond = createOverlayHolder()
        overlayHolderSecond.updateState(IDLE)

        overlayManager.forTestOverlayList.add(overlayHolderSecond)
        `when`(overlayHolderSecond.rootViewGroup.parent).thenReturn(mViewGroupRoot)

        overlayManager.resume()

        assertEquals(DISPLAYING, overlayHolderFirst.state)
        assertEquals(IDLE, overlayHolderSecond.state)
    }

    @Test
    fun whenDestroy() {
        for (i in 0..4) {
            overlayManager.forTestOverlayList.add(createOverlayHolder())
        }

        overlayManager.destroy()

        assertEquals(0, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenPoppingAll_whenPopAll_doNothing() {
        overlayManager.add(createOverlayHolder())
        overlayManager.setIsDismissingAll(true)

        overlayManager.hideAll()

        assertEquals(1, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenAllIdle_whenPopAll_dismissAll() {
        for (i in 0..4) {
            overlayManager.forTestOverlayList.add(createOverlayHolder())
        }

        overlayManager.hideAll()

        assertEquals(0, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenAllAnimatingOutHostResumed_whenPopAll_doNothing() {
        overlayManager.hostResumed()
        for (i in 0..4) {
            val overlayHolder = createOverlayHolder()
            overlayHolder.updateState(ANIMATING_OUT)
            overlayManager.forTestOverlayList.add(overlayHolder)
        }

        overlayManager.hideAll()

        assertEquals(5, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenAllAnimatingOutHostStopped_whenPopAll_dismissAll() {
        overlayManager.hostStopped()
        for (i in 0..4) {
            val overlayHolder = createOverlayHolder()
            overlayHolder.updateState(ANIMATING_OUT)
            overlayManager.forTestOverlayList.add(overlayHolder)
        }

        overlayManager.hideAll()

        assertEquals(0, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenAllActiveHostResumed_whenPopInLoop_runHandler() {
        overlayManager.hostResumed()
        for (i in 0..4) {
            val overlayHolder = createOverlayHolder()
            overlayHolder.updateState(ANIMATING_IN)
            overlayManager.forTestOverlayList.add(overlayHolder)
        }

        overlayManager.dismissFromEndInLoop(5, mHandlerMock, DEFAULT)

        // few verify() in raw do not allow to check if all postDelay() called so we just check
        // latest
        verify<Handler>(mHandlerMock).postDelayed(any(Runnable::class.java), eq(1200L))
    }

    @Test
    fun givenStateOutOrDisplaying_whenSuspendWithRenderer_dismissAll() {
        val overlayHolderAnimOut = createOverlayHolder()
        overlayHolderAnimOut.updateState(ANIMATING_OUT)
        overlayManager.forTestOverlayList.add(overlayHolderAnimOut)

        val overlayHolderDisplaying = createOverlayHolder()
        overlayHolderDisplaying.updateState(DISPLAYING)
        overlayManager.forTestOverlayList.add(overlayHolderDisplaying)

        overlayManager.requestForcedDismissWithRenderer(overlayHolderAnimOut)
        assertEquals("Should remove one with Anim Out", 1, overlayManager.forTestOverlayList.size)
        assertEquals("Wrong holder removed", overlayHolderDisplaying, overlayManager.forTestOverlayList[0])

        overlayManager.requestForcedDismissWithRenderer(overlayHolderDisplaying)
        assertEquals("Should remove second with Displaying", 0, overlayManager.forTestOverlayList.size)
    }

    @Test
    fun givenStateAnimOut_whenSuspendWhilePopAll_dismissAll() {
        for (i in 0..9) {
            val overlayHolderAnimOut = createOverlayHolder()
            overlayHolderAnimOut.updateState(ANIMATING_OUT)
            overlayManager.forTestOverlayList.add(overlayHolderAnimOut)
        }

        overlayManager.suspendWhileDismissingAll(mHandlerMock)

        verify<Handler>(mHandlerMock).removeCallbacksAndMessages(null)

        assertEquals(0, overlayManager.forTestOverlayList.size)
    }
}
