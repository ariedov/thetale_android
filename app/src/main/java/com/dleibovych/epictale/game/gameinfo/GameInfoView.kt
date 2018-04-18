package com.dleibovych.epictale.game.gameinfo

import org.thetale.api.models.GameInfo

interface GameInfoView {

    fun showGameInfo(info: GameInfo)

    fun showError()
}