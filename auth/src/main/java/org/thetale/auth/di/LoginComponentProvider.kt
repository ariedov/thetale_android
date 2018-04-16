package org.thetale.auth.di

interface LoginComponentProvider {

    fun provideLoginComponent(): LoginComponent?

    fun cleanLoginComponent()
}