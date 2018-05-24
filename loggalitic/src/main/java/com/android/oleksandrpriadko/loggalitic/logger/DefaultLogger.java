package com.android.oleksandrpriadko.loggalitic.logger;

import android.util.Log;

import com.android.oleksandrpriadko.loggalitic.Loggalitic;
import com.android.oleksandrpriadko.loggalitic.analytics.policy.Policy;

/**
 * wifi_switcher
 * Created by Oleksandr Priadko.
 * 7/9/17
 */

public class DefaultLogger extends Logger {

    public DefaultLogger(Policy policy) {
        super(policy);
    }

    @Override
    public boolean isLoggable(String tag, int level, boolean force) {
        return getPolicy().isLogAllowed(tag, level, force);
    }

    @Override
    public boolean isLogReportable(String tag, int level, boolean force) {
        return getPolicy().isLogReportAllowed(tag, level, force);
    }

    @Override
    public void v(String tag, String message, boolean force) {
        if (isLoggable(tag, Log.VERBOSE, force)) {
            Log.v(tag, message);
        }
        if (isLogReportable(tag, Log.VERBOSE, force)) {
            Loggalitic.publisher().event(tag, message);
        }
    }

    @Override
    public void d(String tag, String message, boolean force) {
        if (isLoggable(tag, Log.DEBUG, force)) {
            Log.d(tag, message);
        }
        if (isLogReportable(tag, Log.DEBUG, force)) {
            Loggalitic.publisher().event(tag, message);
        }
    }

    @Override
    public void i(String tag, String message, boolean force) {
        if (isLoggable(tag, Log.INFO, force)) {
            Log.i(tag, message);
        }
        if (isLogReportable(tag, Log.INFO, force)) {
            Loggalitic.publisher().event(tag, message);
        }
    }

    @Override
    public void w(String tag, String message, boolean force) {
        if (isLoggable(tag, Log.WARN, force)) {
            Log.w(tag, message);
        }
        if (isLogReportable(tag, Log.WARN, force)) {
            Loggalitic.publisher().event(tag, message);
        }
    }

    @Override
    public void e(String tag, String message, boolean force) {
        if (isLoggable(tag, Log.ERROR, force)) {
            Log.e(tag, message);
        }
        if (isLogReportable(tag, Log.ERROR, force)) {
            Loggalitic.publisher().event(tag, message);
        }
    }

    @Override
    public void v(String tag, String message) {
        v(tag, message, false);
    }

    @Override
    public void d(String tag, String message) {
        d(tag, message, false);
    }

    @Override
    public void i(String tag, String message) {
        i(tag, message, false);
    }

    @Override
    public void w(String tag, String message) {
        w(tag, message, false);
    }

    @Override
    public void e(String tag, String message) {
        e(tag, message, false);
    }

    @Override
    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
    }
}
