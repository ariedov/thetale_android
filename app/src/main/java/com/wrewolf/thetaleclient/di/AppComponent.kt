package com.wrewolf.thetaleclient.di

import com.wrewolf.thetaleclient.activity.MainActivity
import com.wrewolf.thetaleclient.fragment.*
import com.wrewolf.thetaleclient.fragment.dialog.CardUseDialog
import com.wrewolf.thetaleclient.fragment.dialog.QuestActorDialog
import com.wrewolf.thetaleclient.login.di.LoginComponent
import com.wrewolf.thetaleclient.service.WatcherService
import com.wrewolf.thetaleclient.service.widget.AppWidgetProvider
import dagger.Component
import javax.inject.Singleton

@Singleton @Component(modules = [(ApiModule::class)])
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