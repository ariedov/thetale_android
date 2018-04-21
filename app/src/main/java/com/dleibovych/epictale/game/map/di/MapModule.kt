package com.dleibovych.epictale.game.map.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.map.MapDrawer
import com.dleibovych.epictale.game.map.MapPresenter
import com.dleibovych.epictale.game.map.MapProvider
import com.dleibovych.epictale.game.map.MapSpriteProvider
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class MapModule {

    @GameScope
    @Provides
    fun mapRegionProvider(service: TheTaleService): MapProvider = MapProvider(service)

    @GameScope
    @Provides
    fun mapPresenter(gameInfoProvider: GameInfoProvider, mapProvider: MapProvider) = MapPresenter(gameInfoProvider, mapProvider)

    @GameScope
    @Provides
    fun mapSpriteProvider(service: TheTaleService) = MapSpriteProvider(service)

    @GameScope
    @Provides
    fun mapDrawer() = MapDrawer()
}