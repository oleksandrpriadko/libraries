package com.android.oleksandrpriadko.loggalitic;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;
import com.android.oleksandrpriadko.loggalitic.analytics.Publisher;
import com.android.oleksandrpriadko.loggalitic.policy.Policy;
import com.android.oleksandrpriadko.loggalitic.logger.DefaultLogger;
import com.android.oleksandrpriadko.loggalitic.logger.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Oleksandr Priadko.
 * 6/9/17
 * <p>
 * Initialize this class inside your Application class.
 * This class will use provided {@link Publisher} for analytics and {@link Logger} for logging.
 */
public final class Loggalitic {

    private static Publisher analyticsPublisher;

    private static Logger logger;

    public static void init(@NonNull Logger logger) {
        checkNotNull(logger, "Do not pass empty" + Logger.class + " to " + Loggalitic.class);
        Loggalitic.logger = logger;
    }

    public static void init(@NonNull Logger logger, @NonNull Publisher publisher) {
        init(logger);
        checkNotNull(publisher, "Do not pass empty " + Publisher.class + " to " + Loggalitic.class);
        Loggalitic.analyticsPublisher = publisher;
    }

    public static Publisher publisher() {
        if (analyticsPublisher == null) {
            analyticsPublisher = new DummyPublisher(null);
        }
        return analyticsPublisher;
    }

    public static Logger logger() {
        checkNotNull(logger, "Please create " + Logger.class
                + " implementation inside your App class "
                + "or use "
                + DefaultLogger.class
                + " and init " + Loggalitic.class + " with it.");
        return Loggalitic.logger;
    }

    private static class DummyPublisher extends Publisher {

        DummyPublisher(Policy policy) {
            super(policy);
        }

        @Override
        public boolean send(AnalyticsEvent event) {
            Log.e(getTag(), "send: failed as publisher is not initialized");
            return false;
        }
    }
}
