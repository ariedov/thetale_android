package com.dleibovych.epictale.game.map

import kotlinx.coroutines.experimental.async
import org.thetale.api.TheTaleService
import org.thetale.api.call

class MapProvider(val service: TheTaleService) {

    fun getMap(mapVersion: Int) = async {
        return@async service.mapRegion(mapVersion).call()
    }
}