package com.android.oleksandrpriadko.demo.loggalitic;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter;

public class AppCenterConverter extends Converter<Pair<String, Map<String, String>>> {

    @Override
    public Pair<String, Map<String, String>> convert(@NonNull AnalyticsEvent analyticsEvent) {
        String eventName = analyticsEvent.getName();
        Map<String, String> properties = new HashMap<>(10);

        Bundle attrs = analyticsEvent.getAttributes();

        for (String key : attrs.keySet()) {
            if (attrs.get(key) instanceof String) {
                properties.put(key, (String) attrs.get(key));
            }
        }
        return Pair.create(eventName, properties);
    }

    @Override
    public Class getType() {
        return AppCenterConverter.class;
    }
}
