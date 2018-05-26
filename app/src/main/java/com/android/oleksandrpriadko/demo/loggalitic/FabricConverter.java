package com.android.oleksandrpriadko.demo.loggalitic;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter;
import com.crashlytics.android.answers.CustomEvent;

public class FabricConverter extends Converter<CustomEvent> {

    @Override
    public CustomEvent convert(@NonNull AnalyticsEvent analyticsEvent) {
        CustomEvent event = new CustomEvent(analyticsEvent.getName());
        Bundle attrs = analyticsEvent.getAttributes();
        for (String key : attrs.keySet()) {
            if (attrs.get(key) instanceof Number) {
                event.putCustomAttribute(key, (Number) attrs.get(key));
            } else {
                event.putCustomAttribute(key, (String) attrs.get(key));
            }
        }
        return event;
    }

    @Override
    public Class getType() {
        return FabricConverter.class;
    }
}