package com.dleibovych.epictale.game.map

import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MapPresenter(private val gameInfoProvider: GameInfoProvider,
                   private val mapProvider: MapProvider) {

    private val mapVersionRegex = Regex("(\\d+)-(\\d+).(\\d+)")

    private var mapJob: Job? = null

    var view: MapView? = null

    fun loadMap() {
        mapJob = launch(UI) {
            try {
                val gameInfo = gameInfoProvider.getInfo().await()
                val mapVersion = mapVersionRegex.matchEntire(gameInfo.mapVersion)!!.groupValues[1]
                val map = mapProvider.getMap(mapVersion.toInt()).await()
                view?.drawMap(map.region, gameInfo.account!!.hero)
            } catch (e: Exception) {
                view?.showError(e)
            }
        }
    }

    fun dispose() {
        mapJob?.cancel()
    }
}