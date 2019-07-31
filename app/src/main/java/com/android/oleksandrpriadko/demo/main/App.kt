package com.android.oleksandrpriadko.demo.main

import android.app.Application
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.oleksandrpriadko.demo.BuildConfig
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.managers.SharedPreferencesManagerImpl
import com.android.oleksandrpriadko.demo.logpublish.AppCenterConverter
import com.android.oleksandrpriadko.demo.logpublish.DemoPublisher
import com.android.oleksandrpriadko.demo.logpublish.FabricConverter
import com.android.oleksandrpriadko.demo.logpublish.FireBaseConverter
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.loggalitic.logger.DefaultLogger
import com.android.oleksandrpriadko.loggalitic.policy.DefaultPolicy
import com.android.oleksandrpriadko.retrofit.ConnectionStatusReceiver
import com.google.firebase.analytics.FirebaseAnalytics
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)

        fireBaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
        AppCenter.start(this, getString(R.string.api_key_app_centre), Analytics::class.java)

        val policy = initPolicy()

        LogPublishService.init(initLogger(policy), initPublisher(policy))
        CocktailManagerFinder.sharedPreferencesManager = SharedPreferencesManagerImpl(applicationContext)
    }

    private fun initPolicy(): DefaultPolicy {
        val maxAllowedLogLevel = if (BuildConfig.DEBUG) Log.ASSERT else Log.VERBOSE
        val minLogLevelToEvent = if (BuildConfig.DEBUG) Log.ERROR else Log.WARN
        return DefaultPolicy(maxAllowedLogLevel, minLogLevelToEvent, BuildConfig.DEBUG)
    }

    private fun initLogger(policyWiFiSwitcher: DefaultPolicy): DefaultLogger {
        return DefaultLogger(policyWiFiSwitcher)
    }

    private fun initPublisher(policyWiFiSwitcher: DefaultPolicy): DemoPublisher {
        val publisher = DemoPublisher(policyWiFiSwitcher)
        publisher.addConverter(
                FabricConverter(),
                FireBaseConverter(),
                AppCenterConverter())
        return publisher
    }

    private fun registerConnectivityReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectionStatusReceiver, intentFilter)
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            registerConnectivityReceiver()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            unregisterReceiver(connectionStatusReceiver)
        }
    }

    companion object {

        var fireBaseAnalytics: FirebaseAnalytics? = null
            private set

        val connectionStatusReceiver = ConnectionStatusReceiver()
    }
}
