package com.dleibovych.epictale.service.autohelper;

import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.GameInfoUtils;
import com.dleibovych.epictale.util.PreferencesManager;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public class HealthAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
//        return (gameInfoResponse.account.hero.basicInfo.healthCurrent < PreferencesManager.getAutohelpHealthAmountThreshold())
//                && PreferencesManager.shouldAutohelpHealth()
//                && (!PreferencesManager.shouldAutohelpHealthBossOnly() || gameInfoResponse.account.hero.action.isBossFight)
//                && GameInfoUtils.isEnoughEnergy(
//                    gameInfoResponse.account.hero.energy,
//                    PreferencesManager.getAutohelpHealthEnergyThreshold(),
//                    PreferencesManager.shouldAutohelpHealthUseBonusEnergy(),
//                    PreferencesManager.getAutohelpHealthBonusEnergyThreshold());
        return false;
    }

}
