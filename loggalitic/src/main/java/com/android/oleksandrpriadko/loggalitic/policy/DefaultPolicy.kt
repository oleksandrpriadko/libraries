package com.android.oleksandrpriadko.loggalitic.policy

import android.text.TextUtils
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.core.policy.AnalyticsEvent
import com.android.oleksandrpriadko.core.policy.Policy

class DefaultPolicy : Policy {

    constructor() : super(ASSERT, ASSERT, false)

    constructor(logLevel: Int, logToEventLevel: Int, debug: Boolean) : super(logLevel, logToEventLevel, debug)

    override fun isEventAllowed(event: AnalyticsEvent): Boolean {
        if (TextUtils.isEmpty(event.name)) {
            LogPublishService.logger().e(tag, "analytics event not allowed: name of event is empty")
            return false
        }
        return true
    }

    override fun isLogAllowed(tag: String, level: Int, force: Boolean): Boolean {
        return force || isLogAllowed(tag, level)
    }

    override fun isLogReportAllowed(tag: String, level: Int, force: Boolean): Boolean {
        return level >= this.logToEventLevel
    }

    override fun isLogAllowed(tag: String, level: Int): Boolean {
        return level <= this.logLevel
    }
}
