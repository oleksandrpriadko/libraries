package com.android.oleksandrpriadko.demo.loggalitic

import androidx.core.util.Pair
import com.android.oleksandrpriadko.loggalitic.analytics.AnalyticsEvent
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter
import java.util.*

class AppCenterConverter : Converter<Pair<String, Map<String, String>>>() {

    override fun getType(): Class<*> = AppCenterConverter::class.java

    override fun convert(analyticsEvent: AnalyticsEvent): Pair<String, Map<String, String>> {
        val eventName = analyticsEvent.name
        val properties = HashMap<String, String>(10)

        val attrs = analyticsEvent.attributes

        for (key in attrs.keySet()) {
            if (attrs.get(key) is String) {
                properties[key] = attrs.get(key) as String
            }
        }
        return Pair.create(eventName, properties)
    }
}
