package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.GameNavigationProvider
import com.dleibovych.epictale.game.GamePresenter
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameTurnsCache
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class GameModule {

    @GameScope @Provides
    fun navigationProvider() = GameNavigationProvider()

    @GameScope @Provides()
    fun gameTurnsCache() = GameTurnsCache()

    @GameScope @Provides
    fun gameInfoProvider(service: TheTaleService,
                         gameTurnsCache: GameTurnsCache) = GameInfoProvider(service, gameTurnsCache)

    @GameScope @Provides
    fun gamePresenter(provider: GameInfoProvider) = GamePresenter(provider)
}