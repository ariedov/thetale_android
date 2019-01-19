package com.dleibovych.epictale.game.quests

import com.dleibovych.epictale.game.data.GameInfoListener
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import org.thetale.api.TheTaleService
import org.thetale.api.models.GameInfo

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
        gameInfoJob = GlobalScope.launch(Main) {
            try {
                view?.showProgress()
                val info = gameInfoProvider.loadInfo().await()
                view?.showQuests(info)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun chooseQuestOption(option: String) {
        questChoiceJob = GlobalScope.launch(Main) {
            try {
                view?.showQuestActionProgress()
                service.chooseQuestAction(option).join()
                gameInfoScheduler.scheduleImmediate()
            } catch (e: Exception) {
                view?.showQuestActionError()
            }
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