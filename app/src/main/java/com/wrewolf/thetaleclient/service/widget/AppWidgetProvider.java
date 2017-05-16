package com.wrewolf.thetaleclient.service.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;

import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.service.WatcherService;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 13.01.2015
 */
public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if(!WatcherService.isRunning() && !PreferencesManager.shouldServiceStartBoot()) {
            AppWidgetHelper.updateWithError(context, context.getString(R.string.app_widget_not_updated));
        } else {
            AppWidgetHelper.updateWithRequest(context);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        AppWidgetHelper.updateWithRequest(context);
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
