package com.dleibovych.epictale.game.map

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow

class MapProvider(val service: TheTaleService) {

    fun getMap(mapVersion: Int) = GlobalScope.async {
        return@async service.mapRegion(mapVersion).readDataOrThrow()
    }
}