package com.wrewolf.thetaleclient

import com.wrewolf.thetaleclient.di.AppComponent
import com.wrewolf.thetaleclient.login.LoginNavigation
import com.wrewolf.thetaleclient.login.di.LoginComponent
import com.wrewolf.thetaleclient.login.di.LoginNavigationModule

class ComponentProvider {

    lateinit var appComponent: AppComponent
    var loginComponent: LoginComponent? = null

    fun createLoginComponent(navigation: LoginNavigation): LoginComponent {
        loginComponent = appComponent.loginComponent(LoginNavigationModule(navigation))
        return loginComponent!!
    }
}