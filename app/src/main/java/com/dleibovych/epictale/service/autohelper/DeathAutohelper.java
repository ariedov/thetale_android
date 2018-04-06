package com.dleibovych.epictale.service.autohelper;

import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.GameInfoUtils;
import com.dleibovych.epictale.util.PreferencesManager;

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
