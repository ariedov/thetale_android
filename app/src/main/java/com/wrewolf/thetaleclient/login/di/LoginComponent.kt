package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginActivity
import dagger.Subcomponent

@Subcomponent(modules = [(LoginModule::class)])
interface LoginComponent {

    fun inject(target: LoginActivity)
}