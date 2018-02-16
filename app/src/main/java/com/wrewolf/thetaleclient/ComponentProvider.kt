package com.wrewolf.thetaleclient

import com.wrewolf.thetaleclient.di.AppComponent
import com.wrewolf.thetaleclient.login.di.LoginComponent

class ComponentProvider {

    lateinit var appComponent: AppComponent

    var loginComponent: LoginComponent? = null
        get() {
            if (field == null) {
                field = appComponent.loginComponent()
            }
            return field as LoginComponent
        }
}