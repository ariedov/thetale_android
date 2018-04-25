package com.dleibovych.epictale.game.quests

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class QuestsPresenter(val gameInfoProvider: GameInfoProvider,
                      val gameInfoScheduler: GameInfoScheduler) {

    private var gameInfoJob: Job? = null

    var view: QuestsView? = null

    fun start() {
        gameInfoJob = launch(UI) {
            try {
                val info = gameInfoProvider.loadInfo().await()
                view?.showQuests(info)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun stop() {

    }

    fun dispose() {
        gameInfoJob?.cancel()
    }
}