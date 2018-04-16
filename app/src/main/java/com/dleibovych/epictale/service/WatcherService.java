package com.dleibovych.epictale.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import com.dleibovych.epictale.DataViewMode;
import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleApplication;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.dictionary.Action;
import com.dleibovych.epictale.api.model.DiaryEntry;
import com.dleibovych.epictale.api.request.AbilityUseRequest;
import com.dleibovych.epictale.api.request.GameInfoRequest;
import com.dleibovych.epictale.api.response.CommonResponse;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.service.autohelper.Autohelper;
import com.dleibovych.epictale.service.autohelper.CompanionCareAutohelper;
import com.dleibovych.epictale.service.autohelper.DeathAutohelper;
import com.dleibovych.epictale.service.autohelper.EnergyAutohelper;
import com.dleibovych.epictale.service.autohelper.HealthAutohelper;
import com.dleibovych.epictale.service.autohelper.IdlenessAutohelper;
import com.dleibovych.epictale.service.watcher.CardTaker;
import com.dleibovych.epictale.service.watcher.GameStateWatcher;
import com.dleibovych.epictale.service.widget.AppWidgetHelper;
import com.dleibovych.epictale.util.NotificationManager;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.TextToSpeechUtils;
import com.dleibovych.epictale.util.onscreen.OnscreenPart;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 10.10.2014
 */
public class WatcherService extends Service {

    public static final String BROADCAST_SERVICE_RESTART_REFRESH_ACTION =
            TheTaleApplication.Companion.getContext().getPackageName() + ".service.restart.refresh";
    public static final String BROADCAST_WIDGET_HELP_ACTION =
            TheTaleApplication.Companion.getContext().getPackageName() + ".widget.help";
    public static final String BROADCAST_WIDGET_REFRESH_ACTION =
            TheTaleApplication.Companion.getContext().getPackageName() + ".widget.refresh";

    @Inject OkHttpClient client;
    @Inject CookieManager cookieManager;

    private static boolean isRunning = false;

    private static final double INTERVAL_MULTIPLIER = (Math.sqrt(5.0) + 1) / 2.0; // phi
    private static final long INTERVAL_MAX = 600;
    private double intervalMultiplierCurrent;

    private final Handler handler = new Handler();
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (PreferencesManager.isWatcherEnabled()) {
                new GameInfoRequest(client, cookieManager, false).execute(new ApiResponseCallback<GameInfoResponse>() {
                    @Override
                    public void processResponse(GameInfoResponse response) {
                        if (response.account == null) {
                            AppWidgetHelper.updateWithError(WatcherService.this, getString(R.string.game_not_authorized));
                            stopSelf();
                            return;
                        }

                        TheTaleApplication.Companion.getNotificationManager().notify(response);

                        for (final GameStateWatcher watcher : watchers) {
                            watcher.processGameState(response);
                        }

                        boolean shouldHelp = false;
                        for (final Autohelper autohelper : autohelpers) {
                            shouldHelp |= autohelper.shouldHelp(response);
                            if (shouldHelp) {
                                break;
                            }
                        }
                        if (shouldHelp) {
                            new AbilityUseRequest(client, cookieManager, Action.HELP).execute(0, null);
                        }

                        final int diarySize = response.account.hero.diary.size();
                        final int lastDiaryTimestamp = PreferencesManager.getLastDiaryEntryRead();
                        if (PreferencesManager.isDiaryReadAloudEnabled()
                                && TheTaleApplication.Companion.getOnscreenStateWatcher().isOnscreen(OnscreenPart.MAIN)
                                && (lastDiaryTimestamp > 0)) {
                            for (int i = 0; i < diarySize; i++) {
                                final DiaryEntry diaryEntry = response.account.hero.diary.get(i);
                                if (diaryEntry.timestamp > lastDiaryTimestamp) {
                                    TextToSpeechUtils.speak(String.format("%s, %s.\n%s",
                                            diaryEntry.date, diaryEntry.time, diaryEntry.text));
                                }
                            }
                        }
                        if (diarySize > 0) {
                            PreferencesManager.setLastDiaryEntryRead(response.account.hero.diary.get(diarySize - 1).timestamp);
                        } else {
                            PreferencesManager.setLastDiaryEntryRead(0);
                        }

                        AppWidgetHelper.update(WatcherService.this, DataViewMode.DATA, response);

                        intervalMultiplierCurrent = 1.0;
                        postRefresh();
                    }

                    @Override
                    public void processError(GameInfoResponse response) {
                        AppWidgetHelper.update(WatcherService.this, DataViewMode.ERROR, response);

                        intervalMultiplierCurrent *= INTERVAL_MULTIPLIER;
                        postRefresh();
                    }
                }, false);
            } else {
                postRefresh();
            }
        }
    };

    private final BroadcastReceiver notificationDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NotificationManager.BROADCAST_NOTIFICATION_DELETE_ACTION.equals(intent.getAction())) {
                TheTaleApplication.Companion.getNotificationManager().onNotificationDelete();
            }
        }
    };
    private final BroadcastReceiver widgetHelpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (BROADCAST_WIDGET_HELP_ACTION.equals(intent.getAction())) {
                AppWidgetHelper.update(context, DataViewMode.LOADING, null);
                new AbilityUseRequest(client, cookieManager, Action.HELP).execute(0, new ApiResponseCallback<CommonResponse>() {
                    @Override
                    public void processResponse(CommonResponse response) {
                        AppWidgetHelper.updateWithRequest(context, client, cookieManager);
                    }

                    @Override
                    public void processError(CommonResponse response) {
                        AppWidgetHelper.updateWithError(context, response.errorMessage);
                    }
                });
            }
        }
    };
    private final BroadcastReceiver restartRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BROADCAST_SERVICE_RESTART_REFRESH_ACTION.equals(intent.getAction())) {
                restartRefresh();
            }
        }
    };
    private final BroadcastReceiver refreshWidgetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BROADCAST_WIDGET_REFRESH_ACTION.equals(intent.getAction())) {
                AppWidgetHelper.update(context, DataViewMode.LOADING, null);
                restartRefresh();
            }
        }
    };

    private List<GameStateWatcher> watchers;
    private List<Autohelper> autohelpers;

    @Override
    public void onCreate() {
        ((TheTaleApplication) getApplication())
                .getApplicationComponent()
                .inject(this);


        watchers = new ArrayList<>();
        watchers.add(new CardTaker(client, cookieManager));

        autohelpers = new ArrayList<>();
        autohelpers.add(new DeathAutohelper());
        autohelpers.add(new IdlenessAutohelper());
        autohelpers.add(new HealthAutohelper());
        autohelpers.add(new EnergyAutohelper());
        autohelpers.add(new CompanionCareAutohelper());

        registerReceiver(notificationDeleteReceiver, new IntentFilter(NotificationManager.BROADCAST_NOTIFICATION_DELETE_ACTION));
        registerReceiver(widgetHelpReceiver, new IntentFilter(BROADCAST_WIDGET_HELP_ACTION));
        registerReceiver(restartRefreshReceiver, new IntentFilter(BROADCAST_SERVICE_RESTART_REFRESH_ACTION));
        registerReceiver(refreshWidgetReceiver, new IntentFilter(BROADCAST_WIDGET_REFRESH_ACTION));

        intervalMultiplierCurrent = 1.0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        restartRefresh();
        return START_STICKY;
    }

    private void postRefresh() {
        final int intervalInitial = PreferencesManager.getServiceInterval();
        long interval;
        if (intervalInitial >= INTERVAL_MAX) {
            interval = intervalInitial;
            intervalMultiplierCurrent /= INTERVAL_MULTIPLIER;
        } else {
            interval = (long) Math.floor(intervalInitial * intervalMultiplierCurrent);
            if (interval > INTERVAL_MAX) {
                intervalMultiplierCurrent = ((double) INTERVAL_MAX) / ((double) intervalInitial);
                interval = INTERVAL_MAX;
            }
        }

        handler.postDelayed(refreshRunnable, interval * 1000);
    }

    private void restartRefresh() {
        handler.removeCallbacks(refreshRunnable);
        refreshRunnable.run();
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
