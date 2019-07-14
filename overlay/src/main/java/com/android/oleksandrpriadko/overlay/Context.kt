package com.android.oleksandrpriadko.overlay

import android.content.Context
import android.view.ViewGroup

fun Context.overlayManager(): OverlayHelper {
    if (this is OverlayHelper) {
        return this
    } else {
        return object : OverlayHelper {
            override fun isOverlayOnTop(rootViewGroupInOverlayHolder: ViewGroup): Boolean = false

            override fun areAllHidden(): Boolean = false

            override fun isOverlayOnTop(overlay: Overlay): Boolean = false

            override fun addOverlay(overlay: Overlay) {}

            override fun hideLastOverlay(onBackPressed: Boolean): Boolean = false

            override fun hideLastOverlay(): Boolean = false

            override fun hideAllOverlays() {}

            override fun stopOverlayManager() {}

            override fun resumeOverlayManager() {}

            override fun destroyOverlayManager() {}
        }
    }
}