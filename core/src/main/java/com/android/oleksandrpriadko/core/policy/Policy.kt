package com.android.oleksandrpriadko.core.policy

import androidx.annotation.IntDef

/**
 * Determines levels of reporting analytics events and logs. Implement yours or use one
 * from loggalitic module
 */
abstract class Policy
/**
 * @param logLevel        max log level which can be reported. e.g. if mLogLevel is DEBUG -
 * ERROR will not be placed
 * @param logToEventLevel max log level which can de sent to analytics service.
 * @param isDebug           are we in debug mode?
 */
(@param:LogLevel @get:LogLevel
 protected val logLevel: Int,
 @param:LogLevel @get:LogLevel
 protected val logToEventLevel: Int,
 val isDebug: Boolean) {

    protected val tag: String
        get() = this.javaClass.simpleName

    abstract fun isEventAllowed(event: AnalyticsEvent): Boolean

    abstract fun isLogAllowed(tag: String, level: Int, force: Boolean): Boolean

    abstract fun isLogReportAllowed(tag: String, level: Int, force: Boolean): Boolean

    abstract fun isLogAllowed(tag: String, level: Int): Boolean

    @IntDef(VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT)
    annotation class LogLevel

    companion object {

        const val VERBOSE = 2
        const val DEBUG = 3
        const val INFO = 4
        const val WARN = 5
        const val ERROR = 6
        const val ASSERT = 7

        val NOT_SET = object : Policy(
                logLevel = ASSERT,
                logToEventLevel = ASSERT,
                isDebug = false) {
            override fun isEventAllowed(event: AnalyticsEvent): Boolean = false

            override fun isLogAllowed(tag: String, level: Int, force: Boolean): Boolean = false

            override fun isLogReportAllowed(tag: String, level: Int, force: Boolean): Boolean = false

            override fun isLogAllowed(tag: String, level: Int): Boolean = false

        }
    }
}
