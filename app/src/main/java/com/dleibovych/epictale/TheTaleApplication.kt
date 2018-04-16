package com.dleibovych.epictale

import android.app.Application
import android.content.Context

import com.crashlytics.android.Crashlytics
import com.dleibovych.epictale.di.AppInfoModule
import com.google.firebase.analytics.FirebaseAnalytics
import com.dleibovych.epictale.api.cache.RequestCacheManager
import com.dleibovych.epictale.di.ComponentModule
import com.dleibovych.epictale.di.DaggerAppComponent
import com.dleibovych.epictale.util.NotificationManager
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.map.MapUtils
import com.dleibovych.epictale.util.onscreen.OnscreenStateWatcher

import org.thetale.api.di.ApiModule

import io.fabric.sdk.android.Fabric

class TheTaleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        PreferencesManager.init(this)

        context = applicationContext
        analytics = FirebaseAnalytics.getInstance(context!!)
        onscreenStateWatcher = OnscreenStateWatcher()
        notificationManager = NotificationManager(context!!)

        val appComponent = DaggerAppComponent
                .builder()
                .apiModule(ApiModule(this))
                .appInfoModule(AppInfoModule(resources))
                .build()

    }

    override fun onLowMemory() {
        super.onLowMemory()

        RequestCacheManager.invalidate()
        MapUtils.cleanup()

        System.gc()
    }

    companion object {

        var context: Context? = null
            private set
        var componentProvider: ComponentProvider? = null
            private set
        var onscreenStateWatcher: OnscreenStateWatcher? = null
            private set
        var notificationManager: NotificationManager? = null
            private set
        var analytics: FirebaseAnalytics? = null
            private set

        val freeMemory: Long
            get() = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()
    }

}
