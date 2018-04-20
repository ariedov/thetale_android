package com.dleibovych.epictale.game.gameinfo

import org.thetale.api.models.GameInfo

interface GameInfoView {

    fun showProgress()

    fun showGameInfo(info: GameInfo)

    fun showAbilityProgress()

    fun showAbilityError()

    fun showError()
}