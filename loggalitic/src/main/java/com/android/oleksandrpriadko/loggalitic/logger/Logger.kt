package com.android.oleksandrpriadko.loggalitic.logger

import com.android.oleksandrpriadko.loggalitic.policy.DefaultPolicy
import com.android.oleksandrpriadko.loggalitic.policy.Policy

abstract class Logger(protected val policy: Policy) {

    abstract fun isLoggable(tag: String, level: Int, force: Boolean): Boolean

    abstract fun isLogReportable(tag: String, level: Int, force: Boolean): Boolean

    abstract fun v(tag: String, message: String, force: Boolean)

    abstract fun d(tag: String, message: String, force: Boolean)

    abstract fun i(tag: String, message: String, force: Boolean)

    abstract fun w(tag: String, message: String, force: Boolean)

    abstract fun e(tag: String, message: String, force: Boolean)

    abstract fun v(tag: String, message: String)

    abstract fun d(tag: String, message: String)

    abstract fun i(tag: String, message: String)

    abstract fun w(tag: String, message: String)

    abstract fun e(tag: String, message: String)

    abstract fun wtf(tag: String, message: String)

    private class DummyLogger(policy: Policy) : Logger(policy) {

        override fun isLoggable(tag: String, level: Int, force: Boolean): Boolean {
            return false
        }

        override fun isLogReportable(tag: String, level: Int, force: Boolean): Boolean {
            return false
        }

        override fun v(tag: String, message: String, force: Boolean) {

        }

        override fun d(tag: String, message: String, force: Boolean) {

        }

        override fun i(tag: String, message: String, force: Boolean) {

        }

        override fun w(tag: String, message: String, force: Boolean) {

        }

        override fun e(tag: String, message: String, force: Boolean) {

        }

        override fun v(tag: String, message: String) {

        }

        override fun d(tag: String, message: String) {

        }

        override fun i(tag: String, message: String) {

        }

        override fun w(tag: String, message: String) {

        }

        override fun e(tag: String, message: String) {

        }

        override fun wtf(tag: String, message: String) {

        }
    }

    companion object {

        val NOT_SET: Logger = DummyLogger(DefaultPolicy())
    }

}
