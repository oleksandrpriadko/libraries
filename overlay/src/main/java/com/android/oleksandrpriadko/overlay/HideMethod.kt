package com.android.oleksandrpriadko.overlay

enum class HideMethod(val method: String) {

    /**
     * Request to hide [Overlay] by pressing on back button
     */
    BACK_PRESSED("BACK_PRESSED"),

    /**
     * Request to hide [Overlay] by clicking on [Overlay.backgroundView]
     */
    CLICK_ON_BACKGROUND("CLICK_ON_BACKGROUND"),

    /**
     * Request to hide [Overlay] by calling [OverlayManager.hideAll]
     */
    HIDE_ALL("HIDE_ALL"),

    /**
     * Request to hide [Overlay] normally
     */
    DEFAULT("DEFAULT")
}