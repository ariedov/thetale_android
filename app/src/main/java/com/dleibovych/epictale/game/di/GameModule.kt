package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.GameNavigationProvider
import dagger.Module
import dagger.Provides

@Module
class GameModule {

    @GameScope @Provides
    fun navigationProvider() = GameNavigationProvider()
}