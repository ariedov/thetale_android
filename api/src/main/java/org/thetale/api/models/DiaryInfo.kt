package org.thetale.api.models

import com.google.gson.annotations.SerializedName

data class DiaryInfo(
        val version: Int,
        val messages: List<DiaryMessage>
)

data class DiaryMessage(
        val timestamp: Double,
        @SerializedName("game_time") val gameTime: String,
        @SerializedName("game_date") val gameDate: String,
        val message: String,
        val type: Int?,
//        "variables": {"строка": "строка"},  // словарь соотношения переменных и их значений (ВНИМАНИЕ! перечень переменных может изменяться без изменения версии этого метода)
        val position: String
)
