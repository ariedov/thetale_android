package com.dleibovych.epictale.game

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.call

class GamePresenter(private val service: TheTaleService) {

    var view: GameView? = null

    private var gameInfoJob: Job? = null

    fun reload() {
        gameInfoJob = launch(UI) {
            try {
                val info = service.gameInfo().call()
                view?.setGameInfo(info)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun dispose() {
        gameInfoJob?.cancel()
    }
}