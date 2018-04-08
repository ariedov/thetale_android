package com.dleibovych.epictale

import com.dleibovych.epictale.di.AppComponent
import com.dleibovych.epictale.game.di.GameComponent
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

    var gameComponent: GameComponent? = null
            get() {
                if (field == null) {
                    field = appComponent.gameComponent()
                }
                return field as GameComponent
            }
}
