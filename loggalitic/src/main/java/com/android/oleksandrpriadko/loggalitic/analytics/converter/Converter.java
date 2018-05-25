package com.android.oleksandrpriadko.loggalitic.analytics.converter;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;


/**
 * Created by Oleksandr Priadko.
 * 7/9/17
 * <p>
 * Convert generic event to concrete implementation.
 */

public interface Converter<E> {

    String FABRIC = "FABRIC";
    String FIRE_BASE = "FIRE_BASE";

    E convert(@NonNull AnalyticsEvent analyticsEvent);

    @Type
    String getType();

    @StringDef({FABRIC, FIRE_BASE})
    public @interface Type {
    }
}
