package com.android.oleksandrpriadko.loggalitic.policy;

import androidx.annotation.IntDef;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;

/**
 * Determines levels of reporting analytics events and logs. Implement yours or
 * use {@link DefaultPolicy}
 */
public abstract class Policy {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private int mLogLevel;
    private int mLogToEventLevel;
    private boolean mIsDebug;

    /**
     * @param logLevel        max log level which can be reported. e.g. if mLogLevel is DEBUG -
     *                        ERROR will not be placed
     * @param logToEventLevel max log level which can de sent to analytics service.
     * @param debug           are we in debug mode?
     */
    public Policy(@LogLevel int logLevel, @LogLevel int logToEventLevel, boolean debug) {
        this.mLogLevel = logLevel;
        this.mLogToEventLevel = logToEventLevel;
        this.mIsDebug = debug;
    }
    @LogLevel
    protected final int getLogLevel() {
        return this.mLogLevel;
    }

    @LogLevel
    protected final int getLogToEventLevel() {
        return this.mLogToEventLevel;
    }

    public abstract boolean isEventAllowed(AnalyticsEvent event);

    public abstract boolean isLogAllowed(String tag, int level, boolean force);

    public abstract boolean isLogReportAllowed(String tag, int level, boolean force);

    public abstract boolean isLogAllowed(String tag, int level);

    public final boolean isDebug() {
        return mIsDebug;
    }

    protected final String getTag() {
        return this.getClass().getSimpleName();
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    public @interface LogLevel {

    }
}
