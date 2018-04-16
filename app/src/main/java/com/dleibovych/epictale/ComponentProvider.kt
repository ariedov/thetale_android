package com.dleibovych.epictale

import com.dleibovych.epictale.di.AppComponent
import com.dleibovych.epictale.game.di.GameComponent
import org.thetale.auth.di.LoginComponent
import org.thetale.auth.di.LoginComponentProvider

class ComponentProvider(private val appComponent: AppComponent) : LoginComponentProvider {

    private var loginComponent: LoginComponent? by ComponentDelegate { appComponent.loginComponent() }

    override fun provideLoginComponent(): LoginComponent? = loginComponent

    var gameComponent: GameComponent? = null
        get() {
            if (field == null) {
                field = appComponent.gameComponent()
            }
            return field as GameComponent
        }
}
