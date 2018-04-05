package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginNavigation
import dagger.Module
import dagger.Provides

@Module
@LoginScope
class LoginNavigationModule(private val navigation: LoginNavigation) {

    @LoginScope @Provides
    fun loginNavigation() = navigation
}