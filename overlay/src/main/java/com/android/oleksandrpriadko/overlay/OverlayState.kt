package com.android.oleksandrpriadko.overlay

enum class OverlayState(val description: String) {

    /**
     * View is not displayed and not playing any in/out animations
     */
    IDLE("IDLE"),

    /**
     * View is preparing to play or playing inner animations
     */
    DISPLAY_IN_PROGRESS("DISPLAY_IN_PROGRESS"),

    /**
     * View finished inner animations and have been displayed
     */
    DISPLAYING("DISPLAYING"),

    /**
     * View is preparing to play or playing outer animations
     */
    DISMISS_IN_PROGRESS("DISMISS_IN_PROGRESS"),

    /**
     * View is preparing to play or playing outer animations after click on background
     */
    DISMISS_IN_PROGRESS_BACKGROUND_CLICK("DISMISS_IN_PROGRESS_BACKGROUND_CLICK"),

    /**
     * View finished outer animations and have been removed from [.mRootViewGroup]
     */
    DISMISSED("DISMISSED"),

    /**
     * View finished outer animations and have been removed from [.mRootViewGroup] after click on
     * background
     */
    DISMISSED_BACKGROUND_CLICK("DISMISSED_BACKGROUND_CLICK")
}