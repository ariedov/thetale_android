package com.dleibovych.epictale.game.data

import kotlinx.coroutines.experimental.async
import org.thetale.api.TheTaleService
import org.thetale.api.call
import org.thetale.api.models.GameInfo

// TODO: implement game info converting and cache
class GameInfoProvider(private val service: TheTaleService,
                       private val turnsCache: GameTurnsCache) {

    private lateinit var info: GameInfo

    fun getInfo() = async {
        info = service.gameInfo(clientTurns = turnsCache.concatIds()).call()
        turnsCache.saveTurn(info.turn)
        return@async info
    }
}