package com.android.oleksandrpriadko.demo.loggalitic

import com.android.oleksandrpriadko.core.policy.AnalyticsEvent
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter
import com.crashlytics.android.answers.CustomEvent

class FabricConverter : Converter<CustomEvent>() {

    override fun getType(): Class<*> = FabricConverter::class.java

    override fun convert(analyticsEvent: AnalyticsEvent): CustomEvent {
        val event = CustomEvent(analyticsEvent.name)
        val attrs = analyticsEvent.attributes
        for (key in attrs.keySet()) {
            if (attrs.get(key) is Number) {
                event.putCustomAttribute(key, attrs.get(key) as Number)
            } else {
                event.putCustomAttribute(key, attrs.get(key) as String)
            }
        }
        return event
    }
}