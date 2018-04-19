package com.dleibovych.epictale.game.map

import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.core.PresenterState

class MapPresenter(private val gameInfoProvider: GameInfoProvider,
                   private val mapProvider: MapProvider) {

    private val mapVersionRegex = Regex("(\\d+)-(\\d+).(\\d+)")

    private val state = PresenterState { loadMap() }
    private var mapJob: Job? = null

    var view: MapView? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    private fun loadMap() {
        mapJob = launch(UI) {
            try {
                val gameInfo = gameInfoProvider.getInfo().await()
                val mapVersion = mapVersionRegex.matchEntire(gameInfo.mapVersion)!!.groupValues[1]
                val map = mapProvider.getMap(mapVersion.toInt()).await()!!
                state.apply { view?.drawMap(map.region, gameInfo.account!!.hero) }
            } catch (e: Exception) {
                state.apply { view?.showError(e) }
            }
        }
    }

    fun dispose() {
        mapJob?.cancel()
    }
}