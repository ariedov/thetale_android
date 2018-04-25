package com.dleibovych.epictale.game.quests

import org.thetale.api.models.GameInfo

interface QuestsView {

    fun showQuests(info: GameInfo)

    fun showError()
}