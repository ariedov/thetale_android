package com.dleibovych.epictale.game.data

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.thetale.api.models.GameInfo

private const val REFRESH_DELAY = 10_000L

class GameInfoScheduler(private val gameInfoProvider: GameInfoProvider) {

    private val listeners = mutableListOf<GameInfoListener>()
    private var scheduleJob: Job? = null

    fun scheduleImmediate() {
        GlobalScope.launch(Main) {
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

    private fun startScheduling() = GlobalScope.launch(Main) {
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