package com.android.oleksandrpriadko.core.policy

import android.os.Bundle

/**
 * Generic event which have to be converted to necessary type.
 */
class AnalyticsEvent(val name: String) {
    val attributes = Bundle()

    constructor(name: String, description: String) : this(name) {
        this.attributes.putString(DESCRIPTION, description)
    }

    companion object {

        private const val DESCRIPTION = "description"
    }
}
