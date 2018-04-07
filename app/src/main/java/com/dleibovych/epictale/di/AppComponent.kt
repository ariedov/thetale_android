package com.dleibovych.epictale.di

import com.dleibovych.epictale.activity.MainActivity
import com.dleibovych.epictale.fragment.*
import com.dleibovych.epictale.fragment.dialog.CardUseDialog
import com.dleibovych.epictale.fragment.dialog.QuestActorDialog
import com.dleibovych.epictale.login.di.LoginComponent
import com.dleibovych.epictale.login.di.LoginNavigationModule
import com.dleibovych.epictale.service.WatcherService
import com.dleibovych.epictale.service.widget.AppWidgetProvider
import dagger.Component
import javax.inject.Singleton

@Singleton @Component(modules = [ApiModule::class, AppInfoModule::class])
interface AppComponent {

    fun inject(target: MainActivity)

    fun inject(target: GameFragment)

    fun inject(target: CardsFragment)

    fun inject(target: ProfileFragment)

    fun inject(target: EquipmentFragment)

    fun inject(target: DiaryFragment)

    fun inject(target: MapFragment)

    fun inject(target: CardUseDialog)

    fun inject(target: QuestsFragment)

    fun inject(target: GameInfoFragment)

    fun inject(target: QuestActorDialog)

    fun inject(target: ChatFragment)

    fun inject(target: AppWidgetProvider)

    fun inject(target: WatcherService)

    fun loginComponent(): LoginComponent
}