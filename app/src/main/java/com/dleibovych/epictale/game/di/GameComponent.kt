package com.dleibovych.epictale.game.di

import com.dleibovych.epictale.game.gameinfo.GameInfoFragment
import com.dleibovych.epictale.game.MainActivity
import com.dleibovych.epictale.game.gameinfo.di.GameInfoModule
import com.dleibovych.epictale.game.map.MapFragment
import com.dleibovych.epictale.game.map.di.MapModule
import com.dleibovych.epictale.game.profile.ProfileFragment
import com.dleibovych.epictale.game.profile.di.ProfileModule
import dagger.Subcomponent

@GameScope
@Subcomponent(modules = [
    GameModule::class,
    GameInfoModule::class,
    MapModule::class,
    ProfileModule::class])
interface GameComponent {

    fun inject(target: MainActivity)

    fun inject(target: GameInfoFragment)

    fun inject(target: MapFragment)

    fun inject(target: ProfileFragment)
}