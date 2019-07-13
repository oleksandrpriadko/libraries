package com.android.oleksandrpriadko.demo.loggalitic

import android.os.Bundle
import androidx.core.util.Pair

import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter

class FireBaseConverter : Converter<Pair<String, Bundle>>() {

    override fun getType(): Class<*> = FireBaseConverter::class.java

    override fun convert(analyticsEvent: AnalyticsEvent): Pair<String, Bundle> {
        return Pair.create(analyticsEvent.name, analyticsEvent.attributes)
    }
}
