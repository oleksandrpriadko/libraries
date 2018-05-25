package com.android.oleksandrpriadko.loggalitic.logger;

import com.android.oleksandrpriadko.loggalitic.policy.Policy;

/**
 * Created by Oleksandr Priadko.
 * 7/9/17
 */

public abstract class Logger {

    private Policy policy;

    public Logger(Policy policy) {
        this.policy = policy;
    }

    protected Policy getPolicy() {
        return this.policy;
    }

    public abstract boolean isLoggable(String tag, int level, boolean force);

    public abstract boolean isLogReportable(String tag, int level, boolean force);

    public abstract void v(String tag, String message, boolean force);

    public abstract void d(String tag, String message, boolean force);

    public abstract void i(String tag, String message, boolean force);

    public abstract void w(String tag, String message, boolean force);

    public abstract void e(String tag, String message, boolean force);

    public abstract void v(String tag, String message);

    public abstract void d(String tag, String message);

    public abstract void i(String tag, String message);

    public abstract void w(String tag, String message);

    public abstract void e(String tag, String message);

    public abstract void wtf(String tag, String message);

}
