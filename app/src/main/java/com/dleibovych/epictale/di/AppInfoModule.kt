package com.dleibovych.epictale.di

import android.content.res.Resources
import com.dleibovych.epictale.R
import dagger.Module
import dagger.Provides
import javax.inject.Named

const val APP_NAME = "app_name"
const val APP_INFO = "app_info"
const val ABOUT = "about"

@Module
class AppInfoModule(private val resources: Resources) {

    @Provides @Named(APP_NAME)
    fun appName() = resources.getString(R.string.app_name)!!

    @Provides @Named(APP_INFO)
    fun appInfo() = resources.getString(R.string.app_description)!!

    @Provides @Named(ABOUT)
    fun about() = resources.getString(R.string.app_about)!!
}