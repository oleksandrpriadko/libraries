package com.android.oleksandrpriadko.demo.loggalitic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter;

public class FireBaseConverter implements Converter<Pair<String, Bundle>> {

    @Override
    public Pair<String, Bundle> convert(@NonNull AnalyticsEvent analyticsEvent) {
        return Pair.create(analyticsEvent.getName(), analyticsEvent.getAttributes());
    }

    @Override
    @Type
    public String getType() {
        return Converter.FIRE_BASE;
    }
}
