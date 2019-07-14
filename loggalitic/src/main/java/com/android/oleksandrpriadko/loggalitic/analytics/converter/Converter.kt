package com.android.oleksandrpriadko.loggalitic.analytics.converter

import com.android.oleksandrpriadko.core.policy.AnalyticsEvent


/**
 * Convert generic event to concrete implementation.
 */

abstract class Converter<E> {

    abstract fun getType(): Class<*>

    abstract fun convert(analyticsEvent: AnalyticsEvent): E

}
