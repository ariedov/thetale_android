package com.dleibovych.epictale.service.notifier;

import android.app.PendingIntent;
import android.content.Context;

import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleApplication;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.game.GameFragment;
import com.dleibovych.epictale.util.GameInfoUtils;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.UiUtils;
import com.dleibovych.epictale.util.onscreen.OnscreenPart;

/**
 * @author Hamster
 * @since 11.12.2014
 */
public class QuestChoiceNotifier implements Notifier {

    private GameInfoResponse gameInfoResponse;

    @Override
    public void setInfo(GameInfoResponse gameInfoResponse) {
        this.gameInfoResponse = gameInfoResponse;
    }

    @Override
    public boolean isNotifying() {
        if(GameInfoUtils.isQuestChoiceAvailable(gameInfoResponse)) {
            if(PreferencesManager.shouldNotifyQuestChoice()
                    && PreferencesManager.shouldShowNotificationQuestChoice()
                    && !TheTaleApplication.Companion.getOnscreenStateWatcher().isOnscreen(OnscreenPart.QUESTS)) {
                return true;
            }
            PreferencesManager.setShouldShowNotificationQuestChoice(false);
        } else {
            PreferencesManager.setShouldShowNotificationQuestChoice(true);
        }
        return false;
    }

    @Override
    public String getNotification(Context context) {
        return context.getString(R.string.notification_quest_choice);
    }

    @Override
    public PendingIntent getPendingIntent(Context context) {
        return UiUtils.getApplicationIntent(context, GameFragment.GamePage.QUESTS, true);
    }

    @Override
    public void onNotificationDelete() {
        PreferencesManager.setShouldShowNotificationQuestChoice(false);
    }

}
