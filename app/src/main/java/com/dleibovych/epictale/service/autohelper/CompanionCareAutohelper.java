package com.dleibovych.epictale.service.autohelper;

import com.dleibovych.epictale.api.dictionary.HeroAction;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.GameInfoUtils;
import com.dleibovych.epictale.util.PreferencesManager;

/**
 * @author Hamster
 * @since 24.02.2015
 */
public class CompanionCareAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
//        return (gameInfoResponse.account.hero.action.type == HeroAction.COMPANION_CARE)
//                && (gameInfoResponse.account.hero.companionInfo.healthCurrent < PreferencesManager.getAutohelpCompanionCareHealthAmountThreshold())
//                && PreferencesManager.shouldAutohelpCompanionCare()
//                && GameInfoUtils.isEnoughEnergy(
//                gameInfoResponse.account.hero.energy,
//                PreferencesManager.getAutohelpCompanionCareEnergyThreshold(),
//                PreferencesManager.shouldAutohelpCompanionCareUseBonusEnergy(),
//                PreferencesManager.getAutohelpCompanionCareBonusEnergyThreshold());

        return false;
    }

}
