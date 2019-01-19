package com.dleibovych.epictale.game.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow
import org.thetale.api.models.GameInfo

// TODO: enable caching
class GameInfoProvider(private val service: TheTaleService,
                       private val turnsCache: GameTurnsCache) {

    private var info: GameInfo? = null

    fun loadInfo() = GlobalScope.async {
        val newInfo = service.gameInfo(/*turnsCache.concatIds()*/).readDataOrThrow()!!
        info = if (info != null) mergeInfo(info!!, newInfo) else newInfo

        turnsCache.saveTurn(info!!.turn)
        return@async info!!
    }

    fun getInfo() = GlobalScope.async {
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