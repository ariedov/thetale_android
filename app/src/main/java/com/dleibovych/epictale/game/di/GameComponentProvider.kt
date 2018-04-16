package com.dleibovych.epictale.game.di

interface GameComponentProvider {

    fun provideGameComponent(): GameComponent?

    fun cleanGameComponent()
}