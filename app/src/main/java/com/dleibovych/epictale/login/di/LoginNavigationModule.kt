package com.dleibovych.epictale.login.di

import com.dleibovych.epictale.login.LoginNavigationProvider
import dagger.Module
import dagger.Provides

@Module
@LoginScope
class LoginNavigationModule {

    @LoginScope @Provides
    fun navigationProvider() = LoginNavigationProvider()
}