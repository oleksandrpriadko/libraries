package com.android.oleksandrpriadko.demo

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailAppDatabase
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.managers.SharedPreferencesmanagerImpl
import com.android.oleksandrpriadko.demo.logpublish.AppCenterConverter
import com.android.oleksandrpriadko.demo.logpublish.DemoPublisher
import com.android.oleksandrpriadko.demo.logpublish.FabricConverter
import com.android.oleksandrpriadko.demo.logpublish.FireBaseConverter
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.loggalitic.logger.DefaultLogger
import com.android.oleksandrpriadko.loggalitic.policy.DefaultPolicy
import com.google.firebase.analytics.FirebaseAnalytics
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        fireBaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
        AppCenter.start(this, getString(R.string.api_key_app_centre), Analytics::class.java)

        val policy = initPolicy()

        LogPublishService.init(initLogger(policy), initPublisher(policy))
        CocktailManagerFinder.databaseManager = Room
                .databaseBuilder(applicationContext, CocktailAppDatabase::class.java, "ingredients")
                .allowMainThreadQueries()
                .build()
        CocktailManagerFinder.sharedPreferencesManager = SharedPreferencesmanagerImpl(applicationContext)
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

    companion object {

        var fireBaseAnalytics: FirebaseAnalytics? = null
            private set
    }
}
