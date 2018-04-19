package com.dleibovych.epictale.game.gameinfo.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.gameinfo.GameInfoPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class GameInfoModule {

    @GameScope
    @Provides
    fun gameInfoPresenter(
            service: TheTaleService,
            provider: GameInfoProvider,
            scheduler: GameInfoScheduler) = GameInfoPresenter(service, provider, scheduler)
}