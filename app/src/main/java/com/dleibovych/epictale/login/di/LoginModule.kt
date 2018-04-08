package com.dleibovych.epictale.login.di

import com.dleibovych.epictale.di.ABOUT
import com.dleibovych.epictale.di.APP_INFO
import com.dleibovych.epictale.di.APP_NAME
import com.dleibovych.epictale.login.LoginNavigation
import com.dleibovych.epictale.login.LoginNavigationProvider
import com.dleibovych.epictale.login.steps.credentials.LoginCredentialsPresenter
import com.dleibovych.epictale.login.steps.status.CheckStatusPresenter
import com.dleibovych.epictale.login.steps.thirdparty.LoginThirdPartyPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService
import javax.inject.Named

@Module
class LoginModule {

    @LoginScope @Provides
    fun navigationProvider() = LoginNavigationProvider()

    @Provides @LoginScope
    fun thirdPartyPresenter(service: TheTaleService,
                            navigation: LoginNavigationProvider,
                            @Named(APP_NAME) appName: String,
                            @Named(APP_INFO) appInfo: String,
                            @Named(ABOUT) about: String)
            = LoginThirdPartyPresenter(service, navigation, appName, appInfo, about)

    @Provides @LoginScope
    fun checkStatusPresenter(service: TheTaleService,
                             navigation: LoginNavigationProvider)
            = CheckStatusPresenter(service, navigation)

    @Provides @LoginScope
    fun credentialsPresenter(service: TheTaleService,
                             navigation: LoginNavigationProvider)
            = LoginCredentialsPresenter(service, navigation)
}