package com.dleibovych.epictale.di

import com.dleibovych.epictale.game.di.GameComponent
import dagger.Subcomponent
import org.thetale.auth.di.LoginComponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = [ComponentModule::class])
interface HolderComponent {

}