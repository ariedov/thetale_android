package com.wrewolf.thetaleclient.di

import com.wrewolf.thetaleclient.login.di.LoginComponent
import dagger.Component
import javax.inject.Singleton

@Singleton @Component(modules = [(ApiModule::class)])
interface AppComponent {

    fun loginComponent(): LoginComponent
}