package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.gameinfo.GameInfoFragment
import com.dleibovych.epictale.game.MainActivity
import com.dleibovych.epictale.game.gameinfo.di.GameInfoModule
import com.dleibovych.epictale.game.map.MapFragment
import com.dleibovych.epictale.game.map.di.MapModule
import dagger.Subcomponent

@GameScope
@Subcomponent(modules = [GameModule::class, GameInfoModule::class, MapModule::class])
interface GameComponent {

    fun inject(target: MainActivity)

    fun inject(target: GameInfoFragment)

    fun inject(target: MapFragment)
}