package com.android.oleksandrpriadko.loggalitic.policy;

import android.text.TextUtils;
import android.util.Log;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;


/**
 * Created by Oleksandr Priadko.
 * 7/9/17
 */
public class DefaultPolicy extends Policy {

    public DefaultPolicy(int logLevel, int logToEventLevel, boolean debug) {
        super(logLevel, logToEventLevel, debug);
    }

    @Override
    public boolean isEventAllowed(AnalyticsEvent event) {
        if (TextUtils.isEmpty(event.getName())) {
            Log.e(getTag(), "analytics event not allowed: name of event is empty");
            return false;
        }
        return true;
    }

    @Override
    public boolean isLogAllowed(String tag, int level, boolean force) {
        return force || isLogAllowed(tag, level);
    }

    @Override
    public boolean isLogReportAllowed(String tag, int level, boolean force) {
        return level >= this.getLogToEventLevel();
    }

    @Override
    public boolean isLogAllowed(String tag, int level) {
        return level <= this.getLogLevel();
    }
}
