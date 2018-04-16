package com.dleibovych.epictale.di

import com.dleibovych.epictale.ComponentProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ComponentModule(private val appComponent: AppComponent) {

    @Provides @Singleton
    fun componentProvider() = ComponentProvider(appComponent)

}