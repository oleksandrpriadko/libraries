package com.android.oleksandrpriadko.demo;

import android.app.Application;
import android.util.Log;

import com.android.oleksandrpriadko.demo.loggalitic.FabricConverter;
import com.android.oleksandrpriadko.demo.loggalitic.FireBaseConverter;
import com.android.oleksandrpriadko.demo.loggalitic.DemoPublisher;
import com.android.oleksandrpriadko.loggalitic.Loggalitic;
import com.android.oleksandrpriadko.loggalitic.logger.DefaultLogger;
import com.android.oleksandrpriadko.loggalitic.policy.DefaultPolicy;
import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends Application {

    private static FirebaseAnalytics sFireBaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        sFireBaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        DefaultPolicy policy = initPolicy();
        Loggalitic.init(initLogger(policy), initPublisher(policy));
    }

    private DefaultPolicy initPolicy() {
        int maxAllowedLogLevel = BuildConfig.DEBUG ? Log.ASSERT : Log.VERBOSE;
        int minLogLevelToEvent = BuildConfig.DEBUG ? Log.ERROR : Log.WARN;
        return new DefaultPolicy(maxAllowedLogLevel, minLogLevelToEvent, BuildConfig.DEBUG);
    }

    private DefaultLogger initLogger(DefaultPolicy policyWiFiSwitcher) {
        return new DefaultLogger(policyWiFiSwitcher);
    }

    private DemoPublisher initPublisher(DefaultPolicy policyWiFiSwitcher) {
        DemoPublisher publisher = new DemoPublisher(policyWiFiSwitcher);
        publisher.addConverter(new FabricConverter(), new FireBaseConverter());
        return publisher;
    }

    public static FirebaseAnalytics getFireBaseAnalytics() {
        return sFireBaseAnalytics;
    }
}