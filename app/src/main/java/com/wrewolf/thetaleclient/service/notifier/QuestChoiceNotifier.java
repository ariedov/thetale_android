package com.wrewolf.thetaleclient.service.notifier;

import android.app.PendingIntent;
import android.content.Context;

import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.TheTaleClientApplication;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.fragment.GameFragment;
import com.wrewolf.thetaleclient.util.GameInfoUtils;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.UiUtils;
import com.wrewolf.thetaleclient.util.onscreen.OnscreenPart;

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
                    && !TheTaleClientApplication.getOnscreenStateWatcher().isOnscreen(OnscreenPart.QUESTS)) {
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
