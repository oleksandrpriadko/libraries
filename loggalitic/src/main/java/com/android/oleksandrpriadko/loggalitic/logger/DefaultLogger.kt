package com.android.oleksandrpriadko.loggalitic.logger

import android.util.Log
import com.android.oleksandrpriadko.core.logger.Logger
import com.android.oleksandrpriadko.core.policy.Policy
import com.android.oleksandrpriadko.loggalitic.LogPublishService

class DefaultLogger(policy: Policy) : Logger(policy) {

    override fun isLoggable(tag: String?, level: Int, force: Boolean): Boolean {
        return if (tag.isNullOrEmpty()) {
            false
        } else {
            policy.isLogAllowed(tag, level, force)
        }
    }

    override fun isLogReportable(tag: String?, level: Int, force: Boolean): Boolean {
        return if (tag.isNullOrEmpty()) {
            false
        } else {
            return policy.isLogReportAllowed(tag, level, force)
        }
    }

    override fun v(tag: String?, message: String?, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.VERBOSE)
    }

    override fun d(tag: String?, message: String?, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.DEBUG)
    }

    override fun i(tag: String?, message: String?, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.INFO)
    }

    override fun w(tag: String?, message: String?, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.WARN)
    }

    override fun e(tag: String?, message: String?, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.ERROR)
    }

    override fun wtf(tag: String?, message: String?, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.ASSERT)
    }

    private fun requestReportAndSend(tag: String?,
                                     message: String?,
                                     force: Boolean,
                                     @Policy.LogLevel logLevel: Int) {
        if (isLoggable(tag, logLevel, force)) {
            when (logLevel) {
                Policy.VERBOSE -> Log.v(tag, message)
                Policy.DEBUG -> Log.d(tag, message)
                Policy.INFO -> Log.i(tag, message)
                Policy.WARN -> Log.w(tag, message)
                Policy.ERROR -> Log.e(tag, message)
                Policy.ASSERT -> Log.wtf(tag, message)
                else -> {
                }
            }
        }
        if (isLogReportable(tag, logLevel, force)) {
            if (tag != null && message != null) {
                LogPublishService.publisher().event(tag, message)
            }
        }
    }

    override fun v(tag: String?, message: String?) {
        v(tag, message, false)
    }

    override fun d(tag: String?, message: String?) {
        d(tag, message, false)
    }

    override fun i(tag: String?, message: String?) {
        i(tag, message, false)
    }

    override fun w(tag: String?, message: String?) {
        w(tag, message, false)
    }

    override fun e(tag: String?, message: String?) {
        e(tag, message, false)
    }

    override fun wtf(tag: String?, message: String?) {
        Log.wtf(tag, message)
    }
}
