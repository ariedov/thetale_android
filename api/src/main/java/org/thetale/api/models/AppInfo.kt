package org.thetale.api.models

import com.google.gson.annotations.SerializedName

data class AppInfoResponse(
        @SerializedName("data") val data: AppInfo,
        @SerializedName("status") val status: String
)

data class AppInfo(
        @SerializedName("game_version") val gameVersion: String,
        @SerializedName("account_name") val accountName: String?,
        @SerializedName("turn_delta") val turnDelta: Int,
        @SerializedName("abilities_cost") val abilitiesCost: AbilitiesCost,
        @SerializedName("static_content") val staticContent: String,
        @SerializedName("account_id") val accountId: Int?
)

data class AbilitiesCost(
        @SerializedName("arena_pvp_1x1_leave_queue") val arenaPvp1x1LeaveQueue: Int,
        @SerializedName("arena_pvp_1x1") val arenaPvp1x1: Int,
        @SerializedName("help") val help: Int,
        @SerializedName("drop_item") val dropItem: Int,
        @SerializedName("arena_pvp_1x1_accept") val arenaPvp1x1Accept: Int
)