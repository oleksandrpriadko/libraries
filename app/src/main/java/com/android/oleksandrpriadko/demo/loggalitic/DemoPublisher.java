package com.android.oleksandrpriadko.demo.loggalitic;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;

import com.android.oleksandrpriadko.demo.App;
import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;
import com.android.oleksandrpriadko.loggalitic.analytics.Publisher;
import com.android.oleksandrpriadko.loggalitic.policy.Policy;
import com.crashlytics.android.answers.Answers;
import com.microsoft.appcenter.analytics.Analytics;

public class DemoPublisher extends Publisher {

    public DemoPublisher(Policy policy) {
        super(policy);
    }

    @Override
    public boolean send(AnalyticsEvent event) {
        return toFabric(event) && toFireBase(event) && toAppCenter(event);
    }

    private boolean toFabric(AnalyticsEvent event) {
        FabricConverter fabricConverter = findConverter(FabricConverter.class);
        if (fabricConverter != null) {
            Answers.getInstance().logCustom(fabricConverter.convert(event));
            return true;
        } else {
            Log.d(getTag(),
                "send: failed. reason = " + FabricConverter.class.getSimpleName() + " is null");
            return false;
        }
    }

    private boolean toFireBase(AnalyticsEvent event) {
        FireBaseConverter fireBaseConverter = findConverter(FireBaseConverter.class);
        if (fireBaseConverter != null) {
            Pair<String, Bundle> pair = fireBaseConverter.convert(event);
            App.getFireBaseAnalytics().logEvent(pair.first, pair.second);
            return true;
        } else {
            Log.d(getTag(),
                "send: failed. reason = " + FireBaseConverter.class.getSimpleName() + " is null");
            return false;
        }
    }

    private boolean toAppCenter(AnalyticsEvent event) {
        AppCenterConverter appCenterConverter = findConverter(AppCenterConverter.class);
        if (appCenterConverter != null) {
            Pair<String, Map<String, String>> pair = appCenterConverter.convert(event);
            Analytics.trackEvent(pair.first, pair.second);
            return true;
        } else {
            Log.d(getTag(),
                "send: failed. reason = " + AppCenterConverter.class.getSimpleName() + " is null");
            return false;
        }
    }
}