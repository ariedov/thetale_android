package com.dleibovych.epictale.game.map

import android.graphics.Bitmap
import org.thetale.api.models.Hero
import org.thetale.api.models.HeroPosition
import org.thetale.api.models.Region

interface MapView {

    fun showLoading()

    fun drawMap(bitmap: Bitmap, region: Region, hero: HeroPosition)

    fun showError(t: Throwable)
}