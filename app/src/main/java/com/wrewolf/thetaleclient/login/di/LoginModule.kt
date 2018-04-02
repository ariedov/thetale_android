package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginPresenter
import com.wrewolf.thetaleclient.login.steps.chooser.ChooserPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class LoginModule {

    @Provides @LoginScope
    fun loginPresenter(service: TheTaleService): LoginPresenter {
        return LoginPresenter(service)
    }

    @Provides @LoginScope
    fun chooserPresenter(service: TheTaleService): ChooserPresenter {
        return ChooserPresenter(service)
    }
}