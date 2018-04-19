package com.dleibovych.epictale.game.profile.di

import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.profile.ProfilePresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class ProfileModule {

    @Provides @GameScope
    fun profilePresenter(service: TheTaleService,
                         gameProvider: GameInfoProvider) = ProfilePresenter(service, gameProvider)
}