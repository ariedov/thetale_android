package com.dleibovych.epictale.game.diary

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow

class DiaryPresenter(val gameInfoProvider: GameInfoProvider,
                     val gameInfoScheduler: GameInfoScheduler,
                     val service: TheTaleService) {

    private var diaryJob: Job? = null

    var view: DiaryView? = null

    fun start() {
        diaryJob = launch(UI) {
            try {
                val diary = service.diary().readDataOrThrow()!!
                view?.showDiary(diary)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun stop() {

    }

    fun dispose() {
        diaryJob?.cancel()
    }
}