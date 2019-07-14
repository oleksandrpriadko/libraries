package com.android.oleksandrpriadko.overlay

import android.view.ViewGroup
import android.view.ViewParent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OverlayTestBuilder {

    private lateinit var builder: Overlay.Builder

    @Mock
    private lateinit var overlayRootViewGroup: ViewGroup
    @Mock
    private lateinit var backgroundView: ViewGroup
    @Mock
    private lateinit var contentView: ViewGroup

    @Before
    fun setUp() {
        builder = Overlay.Builder(overlayRootViewGroup)
    }

    @Test
    fun backgroundView() {
        builder.backgroundView(backgroundView)
        assertNull("Given child_no_parent -> Builder has null as background", builder.backgroundView)

        `when`<ViewParent>(backgroundView.parent).thenReturn(overlayRootViewGroup)
        builder.backgroundView(backgroundView)
        assertEquals("Given child_valid_parent -> Builder has valid background",
                backgroundView,
                builder.backgroundView)
    }

    @Test
    fun contentView() {
        builder.contentView(contentView)
        assertNull("Given child_no_parent -> Builder has null as content", builder.contentView)

        `when`<ViewParent>(contentView.parent).thenReturn(overlayRootViewGroup)
        builder.contentView(contentView)
        assertEquals("Given child_valid_parent -> Builder has valid content",
                contentView,
                builder.contentView)
    }

    @Test
    fun isChildViewSuitable() {
        var result = builder.isChildViewSuitable(backgroundView, overlayRootViewGroup)
        assertFalse("Given child_no_parent, parent -> Background should be a child of overlayRoot", result)

        result = builder.isChildViewSuitable(backgroundView, backgroundView)
        assertFalse("Given child_no_parent, child_no_parent -> Background should be a child of overlayRoot", result)

        `when`<ViewParent>(backgroundView.parent).thenReturn(overlayRootViewGroup)
        result = builder.isChildViewSuitable(backgroundView, overlayRootViewGroup)
        assertTrue("Given child_valid_parent, parent -> Background should be a child of overlayRoot", result)
    }

    @Test
    fun contains() {
        var result = builder.contains(backgroundView, overlayRootViewGroup)
        assertFalse("Given child_no_parent, parent -> Background should be a child of overlayRoot", result)

        result = builder.contains(backgroundView, backgroundView)
        assertFalse("Given child_no_parent, child_no_parent -> Background should be a child of overlayRoot", result)

        `when`<ViewParent>(backgroundView.parent).thenReturn(overlayRootViewGroup)
        result = builder.contains(backgroundView, overlayRootViewGroup)
        assertTrue("Given child_valid_parent, parent -> Background should be a child of overlayRoot", result)
    }
}