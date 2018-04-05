package com.wrewolf.thetaleclient.login.di

import com.wrewolf.thetaleclient.login.LoginActivity
import com.wrewolf.thetaleclient.login.steps.chooser.LoginChooserFragment
import com.wrewolf.thetaleclient.login.steps.credentials.LoginCredentialsFragment
import com.wrewolf.thetaleclient.login.steps.status.CheckStatusFragment
import com.wrewolf.thetaleclient.login.steps.thirdparty.LoginThirdPartyFragment
import dagger.Subcomponent

@LoginScope @Subcomponent(modules = [LoginModule::class, LoginNavigationModule::class])
interface LoginComponent {

    fun inject(target: LoginActivity)

    fun inject(target: LoginChooserFragment)

    fun inject(target: LoginThirdPartyFragment)

    fun inject(target: CheckStatusFragment)

    fun inject(target: LoginCredentialsFragment)
}