package com.dleibovych.epictale.game.data

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.thetale.api.models.GameInfo

private const val REFRESH_DELAY = 10_000

class GameInfoScheduler(private val gameInfoProvider: GameInfoProvider) {

    private val listeners = mutableListOf<GameInfoListener>()
    private var scheduleJob: Job? = null

    fun scheduleImmediate() {
        launch(UI) {
            scheduleJob?.cancel()
            loadInfo()
            scheduleJob = startScheduling()
        }
    }

    fun addListener(listener: GameInfoListener) {
        listeners.add(listener)

        if (listeners.isNotEmpty()) {
            scheduleJob = startScheduling()
        }
    }

    fun removeListener(listener: GameInfoListener) {
        listeners.remove(listener)

        if (listeners.isEmpty()) {
            scheduleJob?.cancel()
            scheduleJob = null
        }
    }

    private fun startScheduling() = launch(UI) {
        while (this.isActive) {
            delay(REFRESH_DELAY)
            loadInfo()
        }
    }

    private suspend fun loadInfo() {
        val info = gameInfoProvider.loadInfo().await()
        listeners.forEach { it.onGameInfoChanged(info) }
    }
}

interface GameInfoListener {

    fun onGameInfoChanged(info: GameInfo)
}