package com.dleibovych.epictale.login.di

import com.dleibovych.epictale.login.LoginNavigation
import com.dleibovych.epictale.login.steps.credentials.LoginCredentialsPresenter
import com.dleibovych.epictale.login.steps.status.CheckStatusPresenter
import com.dleibovych.epictale.login.steps.thirdparty.LoginThirdPartyPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class LoginModule {

    @Provides @LoginScope
    fun thirdPartyPresenter(service: TheTaleService,
                            navigation: LoginNavigation): LoginThirdPartyPresenter
            = LoginThirdPartyPresenter(service, navigation)

    @Provides @LoginScope
    fun checkStatusPresenter(service: TheTaleService,
                             navigation: LoginNavigation): CheckStatusPresenter
            = CheckStatusPresenter(service, navigation)

    @Provides @LoginScope
    fun credentialsPresenter(service: TheTaleService,
                             navigation: LoginNavigation): LoginCredentialsPresenter
            = LoginCredentialsPresenter(service, navigation)
}