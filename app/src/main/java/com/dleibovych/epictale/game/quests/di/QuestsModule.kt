package com.dleibovych.epictale.game.quests.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.quests.QuestsPresenter
import dagger.Module
import dagger.Provides

@Module
class QuestsModule {

    @Provides @GameScope
    fun questsPresenter(gameInfoProvider: GameInfoProvider,
                        gameInfoScheduler: GameInfoScheduler) = QuestsPresenter(gameInfoProvider, gameInfoScheduler)
}