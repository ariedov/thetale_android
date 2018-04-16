package com.dleibovych.epictale.di

import android.content.res.Resources
import com.dleibovych.epictale.R
import dagger.Module
import dagger.Provides
import org.thetale.auth.di.ABOUT
import org.thetale.auth.di.APP_INFO
import org.thetale.auth.di.APP_NAME
import javax.inject.Named

@Module
class AppInfoModule(private val resources: Resources) {

    @Provides @Named(APP_NAME)
    fun appName() = resources.getString(R.string.app_name)!!

    @Provides @Named(APP_INFO)
    fun appInfo() = resources.getString(R.string.app_description)!!

    @Provides @Named(ABOUT)
    fun about() = resources.getString(R.string.app_about)!!
}