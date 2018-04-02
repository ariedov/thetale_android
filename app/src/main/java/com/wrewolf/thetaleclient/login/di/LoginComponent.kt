package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginActivity
import com.wrewolf.thetaleclient.login.steps.chooser.LoginChooserFragment
import com.wrewolf.thetaleclient.login.views.LoginContentStartView
import com.wrewolf.thetaleclient.login.views.LoginPasswordView
import dagger.Subcomponent

@LoginScope @Subcomponent(modules = [(LoginModule::class)])
interface LoginComponent {

    fun inject(target: LoginActivity)

    fun inject(target: LoginChooserFragment)
}