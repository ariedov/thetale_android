package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.MainActivity
import dagger.Subcomponent

@GameScope
@Subcomponent(modules = [GameModule::class])
interface GameComponent {

    fun inject(target: MainActivity)
}