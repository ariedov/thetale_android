package com.dleibovych.epictale

import com.dleibovych.epictale.di.AppComponent
import com.dleibovych.epictale.login.di.LoginComponent

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
