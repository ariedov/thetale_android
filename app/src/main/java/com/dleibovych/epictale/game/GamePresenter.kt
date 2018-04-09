package com.dleibovych.epictale.game

import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

// TODO: implement logout
class GamePresenter(private val provider: GameInfoProvider) {

    var view: GameView? = null

    private var gameInfoJob: Job? = null

    fun reload() {
        gameInfoJob = launch(UI) {
            try {
                val info = provider.getInfo().await()
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