package com.dleibovych.epictale.game.quests

import org.thetale.api.models.GameInfo

interface QuestsView {

    fun showProgress()

    fun showQuests(info: GameInfo)

    fun showError()

    fun showQuestActionProgress()

    fun showQuestActionError()
}