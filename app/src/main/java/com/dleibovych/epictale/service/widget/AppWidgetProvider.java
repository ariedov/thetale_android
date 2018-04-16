package com.dleibovych.epictale.service.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;

import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleApplication;
import com.dleibovych.epictale.service.WatcherService;
import com.dleibovych.epictale.util.PreferencesManager;

import java.net.CookieManager;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 13.01.2015
 */
public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    @Inject OkHttpClient client;
    @Inject CookieManager cookieManager;

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ((TheTaleApplication)context.getApplicationContext())
                .getApplicationComponent()
                .inject(this);


        if(!WatcherService.isRunning() && !PreferencesManager.shouldServiceStartBoot()) {
            AppWidgetHelper.updateWithError(context, context.getString(R.string.app_widget_not_updated));
        } else {
            AppWidgetHelper.updateWithRequest(context, client, cookieManager);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        ((TheTaleApplication)context.getApplicationContext())
                .getApplicationComponent()
                .inject(this);


        AppWidgetHelper.updateWithRequest(context, client, cookieManager);
    }

    @Override
    public void onEnabled(Context context) {
        PreferencesManager.onWidgetEnabled();
    }

    @Override
    public void onDisabled(Context context) {
        PreferencesManager.onWidgetDisabled();
    }

}
