package com.wrewolf.thetaleclient.service.autohelper;

import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.GameInfoUtils;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public class IdlenessAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
        return GameInfoUtils.isHeroIdle(gameInfoResponse)
                && PreferencesManager.shouldAutohelpIdle()
                && GameInfoUtils.isEnoughEnergy(
                    gameInfoResponse.account.hero.energy,
                    PreferencesManager.getAutohelpIdleEnergyThreshold(),
                    PreferencesManager.shouldAutohelpIdleUseBonusEnergy(),
                    PreferencesManager.getAutohelpIdleBonusEnergyThreshold());
    }

}
