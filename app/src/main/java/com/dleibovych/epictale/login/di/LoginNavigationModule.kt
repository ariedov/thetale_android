package com.dleibovych.epictale.login.di

import com.dleibovych.epictale.login.LoginNavigation
import dagger.Module
import dagger.Provides

@Module
@LoginScope
class LoginNavigationModule(private val navigation: LoginNavigation) {

    @LoginScope @Provides
    fun loginNavigation() = navigation
}