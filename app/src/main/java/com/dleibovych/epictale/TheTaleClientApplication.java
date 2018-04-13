package com.dleibovych.epictale;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.dleibovych.epictale.di.AppInfoModule;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.dleibovych.epictale.api.cache.RequestCacheManager;
import com.dleibovych.epictale.di.AppComponent;
import com.dleibovych.epictale.di.DaggerAppComponent;
import com.dleibovych.epictale.util.NotificationManager;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.map.MapUtils;
import com.dleibovych.epictale.util.onscreen.OnscreenStateWatcher;

import org.thetale.api.di.ApiModule;

import io.fabric.sdk.android.Fabric;

public class TheTaleClientApplication extends Application {

    private static Context context;
    private static ComponentProvider componentProvider;
    private static OnscreenStateWatcher onscreenStateWatcher;
    private static NotificationManager notificationManager;
    private static FirebaseAnalytics analytics;

    public static FirebaseAnalytics getAnalytics() {
        return analytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        PreferencesManager.init(this);

        componentProvider = new ComponentProvider();
        context = getApplicationContext();
        analytics = FirebaseAnalytics.getInstance(context);
        onscreenStateWatcher = new OnscreenStateWatcher();
        notificationManager = new NotificationManager(context);

        AppComponent appComponent = DaggerAppComponent
                .builder()
                .apiModule(new ApiModule(this))
                .appInfoModule(new AppInfoModule(getResources()))
                .build();

        componentProvider.setAppComponent(appComponent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        RequestCacheManager.invalidate();
        MapUtils.cleanup();

        System.gc();
    }

    public static ComponentProvider getComponentProvider() {
        return componentProvider;
    }

    public static Context getContext() {
        return context;
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
    }

    public static OnscreenStateWatcher getOnscreenStateWatcher() {
        return onscreenStateWatcher;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

}
