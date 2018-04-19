package com.dleibovych.epictale.game.data

import kotlinx.coroutines.experimental.async
import org.thetale.api.TheTaleService
import org.thetale.api.call
import org.thetale.api.models.GameInfo

class GameInfoProvider(private val service: TheTaleService,
                       private val turnsCache: GameTurnsCache) {

    private var info: GameInfo? = null

    fun loadInfo() = async {
        info = service.gameInfo(clientTurns = turnsCache.concatIds()).call()
        turnsCache.saveTurn(info!!.turn)

        return@async info!!
    }

    fun getInfo() = async {
        if (info == null) {
            info = loadInfo().await()
        }
        return@async info!!
    }
}