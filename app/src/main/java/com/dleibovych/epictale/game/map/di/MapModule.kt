package com.dleibovych.epictale.game.map.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.map.MapDrawer
import com.dleibovych.epictale.game.map.MapPresenter
import com.dleibovych.epictale.game.map.MapProvider
import com.dleibovych.epictale.game.map.MapSpriteProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.thetale.api.TheTaleService

@Module
class MapModule {

    @GameScope
    @Provides
    fun mapRegionProvider(service: TheTaleService): MapProvider = MapProvider(service)

    @GameScope
    @Provides
    fun mapSpriteProvider(client: OkHttpClient, service: TheTaleService) = MapSpriteProvider(client, service)

    @GameScope
    @Provides
    fun mapDrawer() = MapDrawer()

    @GameScope
    @Provides
    fun mapPresenter(gameInfoProvider: GameInfoProvider,
                     mapProvider: MapProvider,
                     mapSpriteProvider: MapSpriteProvider,
                     mapDrawer: MapDrawer)
            = MapPresenter(gameInfoProvider, mapProvider, mapSpriteProvider, mapDrawer)
}