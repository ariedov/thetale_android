package com.dleibovych.epictale.game.gameinfo

import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.core.PresenterState

class GameInfoPresenter(val provider: GameInfoProvider) {

    private val state = PresenterState { loadGameInfo() }
    private var infoJob: Job? = null

    var view: GameInfoView? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    fun loadGameInfo() {
        infoJob = launch(UI) {
            try {
                val info = provider.getInfo().await()
                state.apply { view?.showGameInfo(info) }
            } catch (e: Exception) {
                state.apply { view?.showError() }
            }
        }
    }

    fun dispose() {
        infoJob?.cancel()
    }
}