package com.dleibovych.epictale.di

import com.dleibovych.epictale.TheTaleAppNavigation
import dagger.Module
import dagger.Provides
import org.thetale.core.AppNavigation
import javax.inject.Singleton

@Module
class NavigationModule {

    @Provides @Singleton
    fun navigation(): AppNavigation = TheTaleAppNavigation()
}