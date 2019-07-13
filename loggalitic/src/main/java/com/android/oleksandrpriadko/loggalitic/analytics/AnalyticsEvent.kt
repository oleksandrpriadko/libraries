package com.android.oleksandrpriadko.loggalitic.analytics

import android.os.Bundle
import android.text.TextUtils

import com.google.common.base.Preconditions.checkArgument

/**
 * Generic event which have to be converted to necessary type.
 */
class AnalyticsEvent(val name: String) {
    val attributes = Bundle()

    init {
        checkArgument(!TextUtils.isEmpty(name))
    }

    constructor(name: String, description: String) : this(name) {
        this.attributes.putString(DESCRIPTION, description)
    }

    companion object {

        private const val DESCRIPTION = "description"
    }
}
