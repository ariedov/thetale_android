package com.dleibovych.epictale.game

import org.thetale.api.models.GameInfo

interface GameView {

    fun setGameInfo(info: GameInfo)

    fun showError()
}