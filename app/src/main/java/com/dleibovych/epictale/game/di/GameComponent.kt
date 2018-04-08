package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.GameActivity
import dagger.Subcomponent

@GameScope
@Subcomponent(modules = [GameModule::class])
interface GameComponent {

    fun inject(target: GameActivity)
}