package com.android.oleksandrpriadko.overlay

enum class OverlayState(val description: String) {

    /**
     * View is not displayed and not playing any in/out animations
     */
    IDLE("IDLE"),

    /**
     * View is playing inner animations
     */
    ANIMATING_IN("ANIMATING_IN"),

    /**
     * View finished inner animations and have been displayed
     */
    DISPLAYING("DISPLAYING"),

    /**
     * View is playing outer animations
     */
    ANIMATING_OUT("ANIMATING_OUT"),

    /**
     * View is playing outer animations after click on background
     */
    ANIMATING_OUT_BACK_CLICK("ANIMATING_OUT_BACK_CLICK"),

    /**
     * View finished outer animations and have been removed from [.mRootViewGroup]
     */
    DISMISSED("DISMISSED"),

    /**
     * View finished outer animations and have been removed from [.mRootViewGroup] after click on
     * background
     */
    DISMISSED_BACK_CLICK("DISMISSED_BACK_CLICK")
}