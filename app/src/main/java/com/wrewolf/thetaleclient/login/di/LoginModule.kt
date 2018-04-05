package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginNavigation
import com.wrewolf.thetaleclient.login.LoginPresenter
import com.wrewolf.thetaleclient.login.steps.chooser.ChooserPresenter
import com.wrewolf.thetaleclient.login.steps.status.CheckStatusPresenter
import com.wrewolf.thetaleclient.login.steps.thirdparty.LoginThirdPartyPresenter
import dagger.Module
import dagger.Provides
import org.thetale.api.TheTaleService

@Module
class LoginModule {

    @Provides @LoginScope
    fun loginPresenter(service: TheTaleService,
                       navigation: LoginNavigation): LoginPresenter
            = LoginPresenter(service, navigation)

    @Provides @LoginScope
    fun chooserPresenter(service: TheTaleService,
                         navigation: LoginNavigation): ChooserPresenter
            = ChooserPresenter(service, navigation)

    @Provides @LoginScope
    fun thirdPartyPresenter(service: TheTaleService,
                            navigation: LoginNavigation): LoginThirdPartyPresenter
            = LoginThirdPartyPresenter(service, navigation)

    @Provides @LoginScope
    fun checkStatusPresenter(service: TheTaleService,
                             navigation: LoginNavigation): CheckStatusPresenter
            = CheckStatusPresenter(service, navigation)
}