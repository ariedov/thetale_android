package com.wrewolf.thetaleclient.service.autohelper;

import com.wrewolf.thetaleclient.api.dictionary.HeroAction;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public class EnergyAutohelper implements Autohelper {

    @Override
    public boolean shouldHelp(GameInfoResponse gameInfoResponse) {
        final HeroAction heroAction = gameInfoResponse.account.hero.action.type;
        return PreferencesManager.shouldAutohelpEnergy()
                && (gameInfoResponse.account.hero.energy.current >= PreferencesManager.getAutohelpEnergyEnergyThreshold()) && (
                    (heroAction == HeroAction.BATTLE) && PreferencesManager.shouldAutohelpEnergyBattle()
                    || (heroAction == HeroAction.RELIGIOUS) && PreferencesManager.shouldAutohelpEnergyReligious()
                    || (heroAction == HeroAction.TRAVEL) && PreferencesManager.shouldAutohelpEnergyTravel()
                );
    }

}
