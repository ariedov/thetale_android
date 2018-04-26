package com.dleibovych.epictale.game.diary.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.data.GameInfoScheduler
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.diary.DiaryPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class DiaryModule {

    @Provides @GameScope
    fun diaryPresenter(gameInfoProvider: GameInfoProvider,
                       gameInfoScheduler: GameInfoScheduler,
                       service: TheTaleService)
            = DiaryPresenter(gameInfoProvider, gameInfoScheduler, service)
}