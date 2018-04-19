package com.dleibovych.epictale.game.data

import kotlinx.coroutines.experimental.async
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow
import org.thetale.api.models.GameInfo

// TODO: enable caching
class GameInfoProvider(private val service: TheTaleService,
                       private val turnsCache: GameTurnsCache) {

    private var info: GameInfo? = null

    fun loadInfo() = async {
        val newInfo = service.gameInfo(/*turnsCache.concatIds()*/).readDataOrThrow()!!
        info = if (info != null) mergeInfo(info!!, newInfo) else newInfo

        turnsCache.saveTurn(info!!.turn)
        return@async info!!
    }

    fun getInfo() = async {
        if (info == null) {
            info = loadInfo().await()
        }
        return@async info!!
    }

    private fun mergeInfo(info: GameInfo, newInfo: GameInfo): GameInfo {
        return GameInfo(
                newInfo.mode,
                newInfo.turn,
                newInfo.gameState,
                newInfo.mapVersion,
                newInfo.account ?: info.account,
                newInfo.enemy ?: info.enemy
        )
    }
}