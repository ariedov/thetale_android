package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginPresenter
import com.wrewolf.thetaleclient.login.steps.chooser.ChooserPresenter
import com.wrewolf.thetaleclient.login.steps.thirdparty.LoginThirdPartyPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class LoginModule {

    @Provides @LoginScope
    fun loginPresenter(service: TheTaleService): LoginPresenter = LoginPresenter(service)

    @Provides @LoginScope
    fun chooserPresenter(service: TheTaleService): ChooserPresenter = ChooserPresenter(service)

    @Provides @LoginScope
    fun thirdPartyPresenter(service: TheTaleService): LoginThirdPartyPresenter = LoginThirdPartyPresenter(service)
}