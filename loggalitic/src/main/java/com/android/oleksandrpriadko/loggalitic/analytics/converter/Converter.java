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

public abstract class Converter<E> {

    public abstract E convert(@NonNull AnalyticsEvent analyticsEvent);

    public abstract Class getType();

}
