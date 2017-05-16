package com.wrewolf.thetaleclient.service.autohelper;

import com.wrewolf.thetaleclient.api.dictionary.HeroAction;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.GameInfoUtils;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 24.02.2015
 */
public class CompanionCareAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
        return (gameInfoResponse.account.hero.action.type == HeroAction.COMPANION_CARE)
                && (gameInfoResponse.account.hero.companionInfo.healthCurrent < PreferencesManager.getAutohelpCompanionCareHealthAmountThreshold())
                && PreferencesManager.shouldAutohelpCompanionCare()
                && GameInfoUtils.isEnoughEnergy(
                gameInfoResponse.account.hero.energy,
                PreferencesManager.getAutohelpCompanionCareEnergyThreshold(),
                PreferencesManager.shouldAutohelpCompanionCareUseBonusEnergy(),
                PreferencesManager.getAutohelpCompanionCareBonusEnergyThreshold());
    }

}
