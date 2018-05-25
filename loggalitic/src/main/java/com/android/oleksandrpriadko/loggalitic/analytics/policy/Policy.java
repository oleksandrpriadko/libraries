package com.android.oleksandrpriadko.loggalitic.analytics.policy;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;

/**
 * Created by Oleksandr Priadko.
 * 7/9/17
 * <p>
 * Determines levels of reporting analytics events and logs. Implement yours or
 * use {@link DefaultPolicy}
 */
public abstract class Policy {

    private int mLogLevel;
    private int mLogToEventLevel;
    private boolean mIsDebug;

    /**
     * @param logLevel        max log level which can be reported. e.g. if mLogLevel is DEBUG -
     *                        ERROR will not be placed
     * @param logToEventLevel max log level which can de sent to analytics service.
     * @param debug           are we in debug mode?
     */
    public Policy(int logLevel, int logToEventLevel, boolean debug) {
        this.mLogLevel = logLevel;
        this.mLogToEventLevel = logToEventLevel;
        this.mIsDebug = debug;
    }

    protected final int getLogLevel() {
        return this.mLogLevel;
    }

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
}
