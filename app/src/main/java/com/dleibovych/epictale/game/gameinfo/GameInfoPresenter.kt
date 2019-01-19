package com.dleibovych.epictale.game.gameinfo

import com.dleibovych.epictale.game.data.GameInfoListener
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.thetale.api.TheTaleService
import org.thetale.api.enumerations.Action
import org.thetale.api.models.GameInfo

class GameInfoPresenter(
        private val service: TheTaleService,
        private val provider: GameInfoProvider,
        private val gameInfoScheduler: GameInfoScheduler) {

    private var infoJob: Job? = null
    private var abilityJob: Job? = null

    var view: GameInfoView? = null

    val listener = object : GameInfoListener {

        override fun onGameInfoChanged(info: GameInfo) {
            view?.showGameInfo(info)
        }
    }

    fun start() {
        gameInfoScheduler.addListener(listener)
        loadGameInfo()
    }

    fun stop() {
        gameInfoScheduler.removeListener(listener)
    }

    fun useAbility(action: Action) {
        abilityJob = GlobalScope.launch(Main) {
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
        loadGameInfo()
    }

    private fun loadGameInfo() {
        infoJob = GlobalScope.launch(Main) {
            try {
                view?.showProgress()
                val info = provider.loadInfo().await()
                view?.showGameInfo(info)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun dispose() {
        infoJob?.cancel()
        abilityJob?.cancel()
    }
}