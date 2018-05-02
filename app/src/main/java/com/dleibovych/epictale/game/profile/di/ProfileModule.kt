package com.dleibovych.epictale.game.profile.di

import com.dleibovych.epictale.game.GameNavigationProvider
import com.dleibovych.epictale.game.data.GameInfoProvider
import com.dleibovych.epictale.game.di.GameScope
import com.dleibovych.epictale.game.profile.ProfilePresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService
import java.net.CookieStore

@Module
class ProfileModule {

    @Provides
    @GameScope
    fun profilePresenter(service: TheTaleService,
                         cookieStore: CookieStore,
                         gameProvider: GameInfoProvider,
                         navigationProvider: GameNavigationProvider)
            = ProfilePresenter(service, cookieStore, gameProvider, navigationProvider)
}