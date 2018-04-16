package com.dleibovych.epictale.service.notifier;

import android.app.PendingIntent;
import android.content.Context;

import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleApplication;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.fragment.GameFragment;
import com.dleibovych.epictale.util.GameInfoUtils;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.UiUtils;
import com.dleibovych.epictale.util.onscreen.OnscreenPart;

/**
 * @author Hamster
 * @since 08.12.2014
 */
public class IdlenessNotifier implements Notifier {

    private GameInfoResponse gameInfoResponse;

    @Override
    public void setInfo(GameInfoResponse gameInfoResponse) {
        this.gameInfoResponse = gameInfoResponse;
    }

    @Override
    public boolean isNotifying() {
        if(GameInfoUtils.isHeroIdle(gameInfoResponse)) {
            if(PreferencesManager.shouldNotifyIdleness()
                    && PreferencesManager.shouldShowNotificationIdleness()
                    && !TheTaleApplication.getOnscreenStateWatcher().isOnscreen(OnscreenPart.GAME_INFO)) {
                return true;
            }
            PreferencesManager.setShouldShowNotificationIdleness(false);
        } else {
            PreferencesManager.setShouldShowNotificationIdleness(true);
        }
        return false;
    }

    @Override
    public String getNotification(Context context) {
        return context.getString(R.string.notification_idle);
    }

    @Override
    public PendingIntent getPendingIntent(Context context) {
        return UiUtils.getApplicationIntent(context, GameFragment.GamePage.GAME_INFO, true);
    }

    @Override
    public void onNotificationDelete() {
        PreferencesManager.setShouldShowNotificationIdleness(false);
    }

}
