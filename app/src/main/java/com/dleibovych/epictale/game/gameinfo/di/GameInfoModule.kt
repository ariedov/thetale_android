package com.dleibovych.epictale.game.gameinfo.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.gameinfo.GameInfoPresenter
import dagger.Module
import dagger.Provides

@Module
class GameInfoModule {

    @GameScope
    @Provides
    fun gameInfoPresenter(provider: GameInfoProvider) = GameInfoPresenter(provider)
}