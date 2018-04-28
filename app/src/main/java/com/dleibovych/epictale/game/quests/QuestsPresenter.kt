package com.dleibovych.epictale.game.quests

import com.dleibovych.epictale.game.data.GameInfoListener
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.models.GameInfo
import org.thetale.core.PresenterState

class QuestsPresenter(val gameInfoProvider: GameInfoProvider,
                      val gameInfoScheduler: GameInfoScheduler,
                      val service: TheTaleService) {

    private var gameInfoJob: Job? = null
    private var questChoiceJob: Job? = null

    private val listener: GameInfoListener = object : GameInfoListener {

        override fun onGameInfoChanged(info: GameInfo) {
            view?.showQuests(info)
        }
    }

    var view: QuestsView? = null

    fun start() {
        loadQuests()

        gameInfoScheduler.addListener(listener)
    }

    fun loadQuests() {
        gameInfoJob = launch(UI) {
            try {
                view?.showProgress()
                val info = gameInfoProvider.loadInfo().await()
                view?.showQuests(info)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun chooseQuestOption(option: Int) {
        questChoiceJob = launch(UI) {
            service.chooseQuestAction(option).join()
            gameInfoScheduler.scheduleImmediate()
        }
    }

    fun stop() {
        gameInfoScheduler.removeListener(listener)
    }

    fun dispose() {
        gameInfoJob?.cancel()
        questChoiceJob?.cancel()
    }
}