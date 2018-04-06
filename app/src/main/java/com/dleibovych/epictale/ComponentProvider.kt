package com.dleibovych.epictale

import com.dleibovych.epictale.di.AppComponent
import com.dleibovych.epictale.login.LoginNavigation
import com.dleibovych.epictale.login.di.LoginComponent
import com.dleibovych.epictale.login.di.LoginNavigationModule

class ComponentProvider {

    lateinit var appComponent: AppComponent
    var loginComponent: LoginComponent? = null

    fun createLoginComponent(navigation: LoginNavigation): LoginComponent {
        loginComponent = appComponent.loginComponent(LoginNavigationModule(navigation))
        return loginComponent!!
    }
}