package com.dleibovych.epictale.game.gameinfo

import com.dleibovych.epictale.game.data.GameInfoListener
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.models.GameInfo
import org.thetale.core.PresenterState

class GameInfoPresenter(private val provider: GameInfoProvider,
                        private val gameInfoScheduler: GameInfoScheduler) {

    private val state = PresenterState { loadGameInfo() }
    private var infoJob: Job? = null
    private var abilityJob: Job? = null

    var view: GameInfoView? = null

    val listener = object : GameInfoListener {

        override fun onGameInfoChanged(info: GameInfo) {
            view?.showGameInfo(info)
        }
    }

    fun start() {
        state.start()

        loadGameInfo()
        gameInfoScheduler.addListener(listener)
    }

    fun stop() {
        state.stop()

        gameInfoScheduler.removeListener(listener)
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