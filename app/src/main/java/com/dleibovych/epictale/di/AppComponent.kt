package com.dleibovych.epictale.di

import com.dleibovych.epictale.fragment.*
import com.dleibovych.epictale.fragment.dialog.CardUseDialog
import com.dleibovych.epictale.fragment.dialog.QuestActorDialog
import com.dleibovych.epictale.game.GameFragment
import com.dleibovych.epictale.game.di.GameComponent
import com.dleibovych.epictale.game.profile.ProfileFragment
import org.thetale.auth.di.LoginComponent
import com.dleibovych.epictale.service.WatcherService
import com.dleibovych.epictale.service.widget.AppWidgetProvider
import dagger.Component
import org.thetale.api.di.ApiModule
import javax.inject.Singleton

@Singleton @Component(modules = [ApiModule::class, AppInfoModule::class,
    ComponentModule::class, NavigationModule::class])
interface AppComponent {

    fun inject(target: GameFragment)

    fun inject(target: CardsFragment)

    fun inject(target: EquipmentFragment)

    fun inject(target: DiaryFragment)

    fun inject(target: CardUseDialog)

    fun inject(target: QuestsFragment)

    fun inject(target: QuestActorDialog)

    fun inject(target: ChatFragment)

    fun inject(target: AppWidgetProvider)

    fun inject(target: WatcherService)

    fun loginComponent(): LoginComponent

    fun gameComponent(): GameComponent
}