package com.android.oleksandrpriadko.loggalitic.analytics.policy;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;

/**
 * baselib
 * Created by Oleksandr Priadko.
 * 7/9/17
 *
 * Determines levels of reporting analytics events and logs. Implement yours or
 * use {@link DefaultPolicy}
 */

public abstract class Policy {

    private int logLevel;
    private int logToEventLevel;
    private boolean isDebug;

    /**
     * @param logLevel          max log level which can be reported. e.g. if logLevel is DEBUG -
     *                          ERROR will not be placed
     * @param logToEventLevel   max log level which can de sent to analytics service.
     * @param debug             are we in debug mode?
     */
    public Policy(int logLevel, int logToEventLevel, boolean debug) {
        this.logLevel = logLevel;
        this.logToEventLevel = logToEventLevel;
        this.isDebug = debug;
    }

    protected int getLogLevel() {
        return this.logLevel;
    }

    protected int getLogToEventLevel() {
        return this.logToEventLevel;
    }

    public abstract boolean isEventAllowed(AnalyticsEvent event);

    public abstract boolean isLogAllowed(String tag, int level, boolean force);

    public abstract boolean isLogReportAllowed(String tag, int level, boolean force);

    public abstract boolean isLogAllowed(String tag, int level);

    public boolean isDebug() {
        return isDebug;
    }

    protected String getTag() {
        return this.getClass().getSimpleName();
    }
}
