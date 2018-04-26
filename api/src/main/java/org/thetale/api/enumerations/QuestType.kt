package org.thetale.api.enumerations

import org.thetale.api.R

enum class QuestType constructor(val code: String, val drawableResId: Int) {

    CARAVAN("caravan", R.drawable.quest_caravan),
    DELIVERY("delivery", R.drawable.quest_delivery),
    HELP("help", R.drawable.quest_help),
    HELP_FRIEND("help_friend", R.drawable.quest_help_friend),
    HOMETOWN("hometown", R.drawable.quest_hometown),
    HUNT("hunt", R.drawable.quest_hunt),
    ENEMY("interfere_enemy", R.drawable.quest_enemy),
    DEBT("collect_debt", R.drawable.quest_debt),
    SPYING("spying", R.drawable.quest_spying),
    SMITH("search_smith", R.drawable.quest_smith),
    NO_QUEST("no-quest", R.drawable.quest_no_quest),
    SPENDING("next-spending", R.drawable.quest_next_spending),
    PILGRIMAGE("pilgrimage", R.drawable.quest_pilgrimage);

    companion object {

        fun getQuestType(code: String) = QuestType.values().first { it.code == code }
    }
}
