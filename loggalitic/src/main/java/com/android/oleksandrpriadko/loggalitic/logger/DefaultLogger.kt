package com.android.oleksandrpriadko.loggalitic.logger

import android.util.Log

import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.loggalitic.policy.Policy

class DefaultLogger(policy: Policy) : Logger(policy) {

    override fun isLoggable(tag: String, level: Int, force: Boolean): Boolean {
        return policy.isLogAllowed(tag, level, force)
    }

    override fun isLogReportable(tag: String, level: Int, force: Boolean): Boolean {
        return policy.isLogReportAllowed(tag, level, force)
    }

    override fun v(tag: String, message: String, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.VERBOSE)
    }

    override fun d(tag: String, message: String, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.DEBUG)
    }

    override fun i(tag: String, message: String, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.INFO)
    }

    override fun w(tag: String, message: String, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.WARN)
    }

    override fun e(tag: String, message: String, force: Boolean) {
        requestReportAndSend(tag, message, force, Policy.ERROR)
    }

    private fun requestReportAndSend(tag: String,
                                     message: String,
                                     force: Boolean,
                                     @Policy.LogLevel logLevel: Int) {
        if (isLoggable(tag, logLevel, force)) {
            Log.e(tag, message)
        }
        if (isLogReportable(tag, logLevel, force)) {
            LogPublishService.publisher().event(tag, message)
        }
    }

    override fun v(tag: String, message: String) {
        v(tag, message, false)
    }

    override fun d(tag: String, message: String) {
        d(tag, message, false)
    }

    override fun i(tag: String, message: String) {
        i(tag, message, false)
    }

    override fun w(tag: String, message: String) {
        w(tag, message, false)
    }

    override fun e(tag: String, message: String) {
        e(tag, message, false)
    }

    override fun wtf(tag: String, message: String) {
        Log.wtf(tag, message)
    }
}
