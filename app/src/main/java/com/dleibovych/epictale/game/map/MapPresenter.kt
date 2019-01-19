package com.dleibovych.epictale.game.map

import android.graphics.Bitmap
import android.graphics.Canvas
import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.thetale.api.enumerations.MapStyle
import org.thetale.api.models.Hero
import org.thetale.api.models.Region
import org.thetale.core.PresenterState

class MapPresenter(private val gameInfoProvider: GameInfoProvider,
                   private val mapProvider: MapProvider,
                   private val mapSpriteProvider: MapSpriteProvider,
                   private val mapDrawer: MapDrawer) {

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

    fun retry() {
        state.apply { loadMap() }
    }

    private fun loadMap() {
        mapJob = GlobalScope.launch(Main) {
            try {
                state.apply { view?.showLoading() }
                val gameInfo = gameInfoProvider.getInfo().await()
                val hero = gameInfo.account!!.hero
                val mapVersion = mapVersionRegex.matchEntire(gameInfo.mapVersion)!!.groupValues[1]
                val map = mapProvider.getMap(mapVersion.toInt()).await()!!
                val region = map.region

                val mapBitmap = createMapBitmap(region, hero)
                state.apply { view?.drawMap(mapBitmap, map.region, hero.position) }
            } catch (e: Exception) {
                state.apply { view?.showError(e) }
            }
        }
    }

    private suspend fun createMapBitmap(region: Region, hero: Hero): Bitmap {
        val sprite = mapSpriteProvider.getMapSprite(MapStyle.STANDARD).await()
        val mapBitmap = mapDrawer.getMapBitmap(region)
        val canvas = Canvas(mapBitmap)
        mapDrawer.drawBaseLayer(canvas, region, sprite)
        mapDrawer.drawPlaceNamesLayer(canvas, region)
        mapDrawer.drawHeroLayer(canvas, hero, sprite)
        return mapBitmap
    }

    fun dispose() {
        mapJob?.cancel()
    }

}
