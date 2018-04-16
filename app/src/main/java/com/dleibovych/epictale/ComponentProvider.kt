package com.dleibovych.epictale

import com.dleibovych.epictale.di.AppComponent
import com.dleibovych.epictale.game.di.GameComponent
import org.thetale.auth.di.LoginComponent

class ComponentProvider(val appComponent: AppComponent) {

    var loginComponent: LoginComponent? by ComponentDelegate { appComponent.loginComponent() }

    var gameComponent: GameComponent? by ComponentDelegate { appComponent.gameComponent() }

}
