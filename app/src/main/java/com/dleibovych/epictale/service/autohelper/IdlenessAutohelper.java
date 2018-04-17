package com.dleibovych.epictale.service.autohelper;

import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.GameInfoUtils;
import com.dleibovych.epictale.util.PreferencesManager;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public class IdlenessAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
//        return GameInfoUtils.isHeroIdle(gameInfoResponse)
//                && PreferencesManager.shouldAutohelpIdle()
//                && GameInfoUtils.isEnoughEnergy(
//                    gameInfoResponse.account.hero.energy,
//                    PreferencesManager.getAutohelpIdleEnergyThreshold(),
//                    PreferencesManager.shouldAutohelpIdleUseBonusEnergy(),
//                    PreferencesManager.getAutohelpIdleBonusEnergyThreshold());
        return false;
    }

}
