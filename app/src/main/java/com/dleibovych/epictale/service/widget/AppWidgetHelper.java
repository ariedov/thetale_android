package com.dleibovych.epictale.service.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dleibovych.epictale.DataViewMode;
import com.dleibovych.epictale.R;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.request.GameInfoRequest;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.service.WatcherService;
import com.dleibovych.epictale.util.RequestUtils;
import com.dleibovych.epictale.util.UiUtils;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 14.01.2015
 */
public class AppWidgetHelper {

    public static void update(final Context context, final DataViewMode viewMode, final GameInfoResponse gameInfoResponse) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        for(final AppWidget appWidget : AppWidget.values()) {
            appWidgetManager.updateAppWidget(
                    new ComponentName(context, appWidget.getProviderClass()),
                    getRemoteViews(context, appWidget, viewMode, gameInfoResponse));
        }
    }

    public static void updateWithRequest(final Context context, OkHttpClient client, CookieManager cookieManager) {
        update(context, DataViewMode.LOADING, null);
        new GameInfoRequest(client, cookieManager, true).execute(new ApiResponseCallback<GameInfoResponse>() {
            @Override
            public void processResponse(GameInfoResponse response) {
                update(context, DataViewMode.DATA, response);
            }

            @Override
            public void processError(GameInfoResponse response) {
                update(context, DataViewMode.ERROR, response);
            }
        }, true);
    }

    public static void updateWithError(final Context context, final String error) {
        try {
            update(context, DataViewMode.ERROR, new GameInfoResponse(RequestUtils.getGenericErrorResponse(error)));
        } catch(JSONException e) {
            update(context, DataViewMode.ERROR, null);
        }
    }

    private static RemoteViews getRemoteViews(final Context context, final AppWidget appWidget,
                                              final DataViewMode viewMode, final GameInfoResponse gameInfoResponse) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), appWidget.getLayoutResId());

        UiUtils.setRemoteViewsViewVisibility(remoteViews, R.id.app_widget_content, viewMode == DataViewMode.DATA);
        UiUtils.setRemoteViewsViewVisibility(remoteViews, R.id.app_widget_progress, viewMode == DataViewMode.LOADING);
        UiUtils.setRemoteViewsViewVisibility(remoteViews, R.id.app_widget_error, viewMode == DataViewMode.ERROR);

        remoteViews.setOnClickPendingIntent(R.id.app_widget, UiUtils.getApplicationIntent(context, null, true));

        switch(viewMode) {
            case DATA:
                appWidget.fillData(context, remoteViews, gameInfoResponse);
                break;

            case ERROR:
                if(gameInfoResponse == null) {
                    remoteViews.setTextViewText(R.id.app_widget_error_text, context.getString(R.string.common_error_short));
                } else {
                    remoteViews.setTextViewText(R.id.app_widget_error_text, gameInfoResponse.errorMessage);
                }

                remoteViews.setOnClickPendingIntent(R.id.app_widget_error_retry, PendingIntent.getBroadcast(
                        context,
                        (int) System.currentTimeMillis(),
                        new Intent(WatcherService.BROADCAST_WIDGET_REFRESH_ACTION),
                        PendingIntent.FLAG_CANCEL_CURRENT));
                break;
        }

        return remoteViews;
    }

}
