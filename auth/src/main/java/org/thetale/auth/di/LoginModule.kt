package org.thetale.auth.di

import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService
import org.thetale.auth.LoginNavigationProvider
import org.thetale.auth.steps.credentials.LoginCredentialsPresenter
import org.thetale.auth.steps.status.CheckStatusPresenter
import org.thetale.auth.steps.thirdparty.LoginThirdPartyPresenter
import javax.inject.Named

const val APP_NAME = "app_name"
const val APP_INFO = "app_info"
const val ABOUT = "about"

@Module
class LoginModule {

    @LoginScope
    @Provides
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