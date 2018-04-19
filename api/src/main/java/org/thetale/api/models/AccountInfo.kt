package org.thetale.api.models

import com.google.gson.annotations.SerializedName

data class AccountInfo(
        val id: Int,
        val registered: Boolean,
        val name: String,
        @SerializedName("hero_id") val heroId: Int,
        @SerializedName("places_history") val places: List<Any>,
        val might: Double,
        val achievements: Int,
        val collections: Int,
        val referrals: Int,
        val rating: Map<String, Rating>,
        val permissions: Permission,
        val description: String,
        val clan: Clan?
)

data class Rating(
        val name: String,
        val place: Int,
        val value: Double
)

data class Permission(
        @SerializedName("can_affect_game") val canAffectGame: Boolean
)

data class Clan(
        val id: Int,
        val abbr: String,
        val name: String
)