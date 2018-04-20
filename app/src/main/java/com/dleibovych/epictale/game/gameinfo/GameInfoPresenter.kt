package com.dleibovych.epictale.game.gameinfo

import com.dleibovych.epictale.game.data.GameInfoListener
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.enumerations.Action
import org.thetale.api.models.GameInfo
import org.thetale.core.PresenterState

class GameInfoPresenter(
        private val service: TheTaleService,
        private val provider: GameInfoProvider,
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
    }

    fun stop() {
        state.stop()

        gameInfoScheduler.removeListener(listener)
    }

    fun useAbility(action: Action) {
        abilityJob = launch(UI) {
            try {
                view?.showAbilityProgress()
                service.useAbility(action.code).join()
                gameInfoScheduler.scheduleImmediate()
            } catch (e: Exception) {
                view?.showAbilityError()
            }
        }


    }

    fun retry() {
        state.apply { loadGameInfo() }
    }

    private fun loadGameInfo() {
        infoJob = launch(UI) {
            try {
                state.apply { view?.showProgress() }
                val info = provider.getInfo().await()
                gameInfoScheduler.addListener(listener)
                state.apply { view?.showGameInfo(info) }
            } catch (e: Exception) {
                state.apply { view?.showError() }
            }
        }
    }

    fun dispose() {
        infoJob?.cancel()
        abilityJob?.cancel()
    }
}