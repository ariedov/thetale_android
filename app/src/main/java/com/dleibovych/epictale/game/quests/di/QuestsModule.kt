package com.dleibovych.epictale.game.quests.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.quests.QuestsPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class QuestsModule {

    @Provides @GameScope
    fun questsPresenter(gameInfoProvider: GameInfoProvider,
                        gameInfoScheduler: GameInfoScheduler,
                        service: TheTaleService)
            = QuestsPresenter(gameInfoProvider, gameInfoScheduler, service)
}