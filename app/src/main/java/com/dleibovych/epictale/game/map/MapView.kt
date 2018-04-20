package com.dleibovych.epictale.game.map

import org.thetale.api.models.Hero
import org.thetale.api.models.Region

interface MapView {

    fun showLoading()

    fun drawMap(region: Region, hero: Hero)

    fun showError(t: Throwable)
}