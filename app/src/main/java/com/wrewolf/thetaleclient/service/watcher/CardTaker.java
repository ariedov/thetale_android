package com.wrewolf.thetaleclient.service.watcher;

import com.wrewolf.thetaleclient.api.request.TakeCardRequest;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 14.11.2014
 */
public class CardTaker implements GameStateWatcher {

    @Override
    public void processGameState(GameInfoResponse gameInfoResponse) {
        if(PreferencesManager.shouldAutoactionCardTake()) {
            if(gameInfoResponse.account.hero.cards.cardHelpCurrent >= gameInfoResponse.account.hero.cards.cardHelpBarrier) {
                new TakeCardRequest().execute(null);
            }
        }
    }

}
