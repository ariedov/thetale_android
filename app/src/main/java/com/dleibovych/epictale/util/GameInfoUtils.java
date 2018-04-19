package com.dleibovych.epictale.util;

import android.content.Context;

import org.thetale.api.enumerations.ArtifactEffect;
import com.dleibovych.epictale.api.model.EnergyInfo;
import com.dleibovych.epictale.api.model.HeroBasicInfo;
import com.dleibovych.epictale.api.model.QuestStepInfo;
import com.dleibovych.epictale.api.response.GameInfoResponse;

import org.thetale.api.models.ArtifactInfo;
import org.thetale.api.models.GameInfo;
import org.thetale.api.models.Hero;
import org.thetale.api.models.HeroAction;

import java.util.List;
import java.util.Map;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public class GameInfoUtils {

    public static boolean isEnoughEnergy(final EnergyInfo energyInfo, final int energyThreshold,
                                         final boolean shouldUseBonusEnergy, final int bonusEnergyThreshold) {
        return (energyInfo.current >= energyThreshold)
                || shouldUseBonusEnergy && (energyInfo.bonus >= bonusEnergyThreshold);
    }

    public static boolean isEnoughEnergy(final EnergyInfo energyInfo, final int need) {
        return (energyInfo.current + energyInfo.bonus - energyInfo.discount) >= need;
    }

    public static boolean isHeroIdle(final GameInfo gameInfoResponse) {
        return gameInfoResponse.getAccount().getHero().getAction().getType() == 0;
    }

    public static boolean isQuestChoiceAvailable(final GameInfoResponse gameInfoResponse) {
        final int questLinesCount = gameInfoResponse.account.hero.quests.size();
        if(questLinesCount > 0) {
            final List<QuestStepInfo> lastQuestLine = gameInfoResponse.account.hero.quests.get(questLinesCount - 1);
            final int questStepsCount = lastQuestLine.size();
            if(questStepsCount > 0) {
                final QuestStepInfo lastQuestStep = lastQuestLine.get(questStepsCount - 1);
                return lastQuestStep.choices.size() > 0;
            }
        }
        return false;
    }

    public static String getHealthString(final HeroBasicInfo heroBasicInfo) {
        return String.format("%d/%d", heroBasicInfo.healthCurrent, heroBasicInfo.healthMax);
    }

    public static String getExperienceString(final HeroBasicInfo heroBasicInfo) {
        return String.format("%d/%d", heroBasicInfo.experienceCurrent, heroBasicInfo.experienceForNextLevel);
    }

    public static String getEnergyString(final EnergyInfo energyInfo) {
        return String.format("%d/%d + %d", energyInfo.current, energyInfo.max, energyInfo.bonus);
    }


    public static String getActionString(final Context context, final HeroAction actionInfo) {
        String actionDescription = actionInfo.getDescription();
        return actionDescription;
    }

    public static int getArtifactEffectCount(final Hero hero, final ArtifactEffect artifactEffect) {
        int count = 0;
        for(final Map.Entry<Integer, ArtifactInfo> equipmentEntry : hero.getEquipment().entrySet()) {
            final ArtifactInfo artifactInfo = equipmentEntry.getValue();
            if(artifactInfo.getEffect() == artifactEffect.getCode()) {
                count++;
            }
            if(artifactInfo.getSpecialEffect() == artifactEffect.getCode()) {
                count++;
            }
        }
        return count;
    }

}
