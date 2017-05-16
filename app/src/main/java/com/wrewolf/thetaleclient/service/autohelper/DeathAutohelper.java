package com.wrewolf.thetaleclient.service.autohelper;

import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.GameInfoUtils;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public class DeathAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
        return !gameInfoResponse.account.hero.basicInfo.isAlive
                && PreferencesManager.shouldAutohelpDeath()
                && GameInfoUtils.isEnoughEnergy(
                    gameInfoResponse.account.hero.energy,
                    PreferencesManager.getAutohelpDeathEnergyThreshold(),
                    PreferencesManager.shouldAutohelpDeathUseBonusEnergy(),
                    PreferencesManager.getAutohelpDeathBonusEnergyThreshold());
    }

}
