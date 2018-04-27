package com.dleibovych.epictale.game.diary

import com.dleibovych.epictale.game.data.GameInfoListener
import com.dleibovych.epictale.game.data.GameInfoScheduler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.models.GameInfo
import org.thetale.api.readDataOrThrow
import org.thetale.core.PresenterState

class DiaryPresenter(private val gameInfoScheduler: GameInfoScheduler,
                     private val service: TheTaleService) {

    private var state = PresenterState { loadDiary() }

    private var diaryJob: Job? = null
    private lateinit var diaryVersion: String

    private val listener = object : GameInfoListener {

        override fun onGameInfoChanged(info: GameInfo) {
            if (diaryVersion != info.account?.hero?.diary) {
                state.apply { loadDiary() }
            }
        }
    }

    var view: DiaryView? = null

    fun start() {
        state.start()

        gameInfoScheduler.addListener(listener)
    }

    fun loadDiary() {
        diaryJob = launch(UI) {
            try {
                state.apply { view?.showProgress() }
                val diary = service.diary().readDataOrThrow()!!
                diaryVersion = diary.version.toString()
                state.apply { view?.showDiary(diary) }
            } catch (e: Exception) {
                state.apply { view?.showError() }
            }
        }
    }

    fun stop() {
        state.stop()

        gameInfoScheduler.removeListener(listener)
    }

    fun dispose() {
        diaryJob?.cancel()
    }
}