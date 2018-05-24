package com.android.oleksandrpriadko.loggalitic.analytics.converter;

import android.support.annotation.NonNull;

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent;


/**
 * baselib
 * Created by Oleksandr Priadko.
 * 7/9/17
 *
 * Convert generic event to concrete implementation.
 */

public interface Converter<E> {

    /**
     * All magic happens here
     * @param analyticsEvent        Generic event
     * @return                      event, converted to necessary form
     */
    E convert(@NonNull AnalyticsEvent analyticsEvent);

    /**
     * Allows to catch converter from list
     */
    TYPE getType();

    /**
     * You can extend this enum or extend FROM this enum for your own type.
     */
    public enum TYPE {
        FABRIC,
        FIREBASE
    }
}
