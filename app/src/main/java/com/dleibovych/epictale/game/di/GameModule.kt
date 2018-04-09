package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.GameNavigationProvider
import com.dleibovych.epictale.game.GamePresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class GameModule {

    @GameScope @Provides
    fun navigationProvider() = GameNavigationProvider()

    @GameScope @Provides
    fun gamePresenter(service: TheTaleService) = GamePresenter(service)
}