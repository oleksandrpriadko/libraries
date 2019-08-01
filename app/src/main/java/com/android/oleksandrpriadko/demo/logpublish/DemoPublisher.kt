package com.android.oleksandrpriadko.demo.logpublish

import android.util.Log
import com.android.oleksandrpriadko.core.policy.AnalyticsEvent
import com.android.oleksandrpriadko.core.policy.Policy
import com.android.oleksandrpriadko.demo.main.App
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.loggalitic.analytics.Publisher
import com.crashlytics.android.answers.Answers
import com.microsoft.appcenter.analytics.Analytics
import io.fabric.sdk.android.Fabric

class DemoPublisher(policy: Policy) : Publisher(policy) {

    override fun send(event: AnalyticsEvent): Boolean {
        return try {
            toFabric(event) && toFireBase(event) && toAppCenter(event)
        } catch (ignore: Exception) {
            false
        }
    }

    private fun toFabric(event: AnalyticsEvent): Boolean {
        val fabricConverter = findConverter<FabricConverter>(FabricConverter::class.java)
        return when {
            fabricConverter != null -> {
                try {
                    Answers.getInstance().logCustom(fabricConverter.convert(event))
                } catch (e: IllegalStateException) {
                    LogPublishService.logger().d(tag,
                            "send: failed. reason = ${Fabric::class.java.simpleName} not initialised")
                    return false
                }
                true
            }
            else -> {
                LogPublishService.logger().d(tag,
                        "send: failed. reason = ${FabricConverter::class.java.simpleName} is null")
                false
            }
        }
    }

    private fun toFireBase(event: AnalyticsEvent): Boolean {
        val fireBaseConverter = findConverter<FireBaseConverter>(FireBaseConverter::class.java)
        return if (fireBaseConverter != null) {
            val pair = fireBaseConverter.convert(event)
            App.fireBaseAnalytics!!.logEvent(pair.first!!, pair.second)
            true
        } else {
            Log.d(tag,
                    "send: failed. reason = ${FireBaseConverter::class.java.simpleName} is null")
            false
        }
    }

    private fun toAppCenter(event: AnalyticsEvent): Boolean {
        val appCenterConverter = findConverter<AppCenterConverter>(AppCenterConverter::class.java)
        return if (appCenterConverter != null) {
            val pair = appCenterConverter.convert(event)
            Analytics.trackEvent(pair.first, pair.second)
            true
        } else {
            LogPublishService.logger().d(tag,
                    "send: failed. reason = ${AppCenterConverter::class.java.simpleName} is null")
            false
        }
    }
}