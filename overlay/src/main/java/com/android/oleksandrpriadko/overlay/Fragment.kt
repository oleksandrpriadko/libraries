package com.android.oleksandrpriadko.overlay

import android.view.ViewGroup
import androidx.fragment.app.Fragment

fun Fragment.overlayManager(): OverlayHelper {
    if (activity is OverlayHelper) {
        return activity as OverlayHelper
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