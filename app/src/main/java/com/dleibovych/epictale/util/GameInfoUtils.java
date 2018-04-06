package com.dleibovych.epictale.util;

import android.content.Context;

import com.dleibovych.epictale.R;
import com.dleibovych.epictale.api.dictionary.ArtifactEffect;
import com.dleibovych.epictale.api.dictionary.EquipmentType;
import com.dleibovych.epictale.api.dictionary.HeroAction;
import com.dleibovych.epictale.api.model.ArtifactInfo;
import com.dleibovych.epictale.api.model.CompanionInfo;
import com.dleibovych.epictale.api.model.EnergyInfo;
import com.dleibovych.epictale.api.model.HeroActionInfo;
import com.dleibovych.epictale.api.model.HeroBasicInfo;
import com.dleibovych.epictale.api.model.HeroInfo;
import com.dleibovych.epictale.api.model.QuestStepInfo;
import com.dleibovych.epictale.api.response.GameInfoResponse;

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

    public static boolean isHeroIdle(final GameInfoResponse gameInfoResponse) {
        return gameInfoResponse.account.hero.action.type == HeroAction.IDLE;
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

    public static String getCompanionHealthString(final CompanionInfo companionInfo) {
        return String.format("%d/%d", companionInfo.healthCurrent, companionInfo.healthMax);
    }

    public static String getCompanionExperienceString(final CompanionInfo companionInfo) {
        return String.format("%d/%d", companionInfo.experienceCurrent, companionInfo.experienceForNextLevel);
    }

    public static String getActionString(final Context context, final HeroActionInfo actionInfo) {
        String actionDescription = actionInfo.description;
        if(actionInfo.isBossFight) {
            actionDescription += context.getString(R.string.game_boss_fight);
        }
        return actionDescription;
    }

    public static int getArtifactEffectCount(final HeroInfo heroInfo, final ArtifactEffect artifactEffect) {
        int count = 0;
        for(final Map.Entry<EquipmentType, ArtifactInfo> equipmentEntry : heroInfo.equipment.entrySet()) {
            final ArtifactInfo artifactInfo = equipmentEntry.getValue();
            if(artifactInfo.effect == artifactEffect) {
                count++;
            }
            if(artifactInfo.effectSpecial == artifactEffect) {
                count++;
            }
        }
        return count;
    }

}
