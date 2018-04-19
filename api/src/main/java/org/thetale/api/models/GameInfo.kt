package org.thetale.api.models

import com.google.gson.annotations.SerializedName

data class GameInfo(
        val mode: String, // pvp, pve
        val turn: Turn,
        @SerializedName("game_state") val gameState: Int, // converts to enumeration
        @SerializedName("map_version") val mapVersion: String,
        val account: HeroAccountInfo?,
        val enemy: HeroAccountInfo?
)

data class Turn(
        val number: Int,
        @SerializedName("verbose_date") val verboseDate: String,
        @SerializedName("verbose_time") val verboseTime: String
)

data class HeroAccountInfo(
        @SerializedName("new_messages") val newMessages: Int,
        val id: Int,
        @SerializedName("last_visit") val lastVisit: Long,
        @SerializedName("is_own") val isOwn: Boolean,
        val hero: Hero,
        val energy: Int?
)

data class Hero(
        @SerializedName("patch_turn") val patchTurn: Int,
        val equipment: Map<Int, ArtifactInfo>,
        val companion: CompanionInfo?,
        val bag: Map<Int, ArtifactInfo>,
        val base: Base,
        val secondary: Secondary,
        val diary: String,
        val messages: List<List<Any>>, // this one is f* up. need to convert
        val habits: Map<String, Habit>,
        val quests: Quests,
        val action: HeroAction,
        val position: HeroPosition,
        val permissions: Permissions,
        val might: Might,
        val id: Int,
        @SerializedName("actual_on_turn") val actualOnTurn: Int,
        val sprite: Int
)

data class ArtifactInfo(
        val name: String,
        val power: List<Int>,
        val type: Int,
        val integrity: List<Int>,
        val rarity: Int,
        val effect: Int,
        @SerializedName("special_effect") val specialEffect: Int,
        @SerializedName("preference_rating") val preferenceRating: Double,
        val equipped: Boolean,
        val id: Int
)

data class CompanionInfo(
        val type: Int,
        val name: String,
        val health: Int,
        @SerializedName("max_health") val maxHealth: Int,
        val experience: Int,
        @SerializedName("experience_to_level") val experienceToLevel: Int,
        val coherence: Int,
        @SerializedName("real_coherence") val realCoherence: Int
)

data class Base(
        val experience: Int,
        val race: Int,
        val health: Int,
        val name: String,
        val level: Int,
        val gender: Int,
        @SerializedName("experience_to_level") val experienceToLevel: Int,
        @SerializedName("max_health") val maxHealth: Int,
        @SerializedName("destiny_points") val destinyPoints: Int,
        val money: Int,
        val alive: Boolean
)

data class Secondary(
        @SerializedName("max_bag_size") val maxBagSize: Int,
        val power: List<Int>,
        @SerializedName("move_speed") val moveSpeed: Double,
        @SerializedName("loot_items_count") val lootItemsCount: Int,
        val initiative: Double
)

data class Habit(
        val verbose: String,
        val raw: Double
)

data class Quests(
        val quests: List<Lines>
)

data class Lines(
        val line: List<Quest>
)

data class Quest(
        val type: String,
        val uid: String,
        val name: String,
        val action: String,
        val choice: String,
        val choiceAlternatives: List<List<String>>,
        val experience: Int,
        val power: Int,
        val actors: List<List<Any>> // f* up
)

data class HeroAction(
        val percents: Double,
        val description: String,
        val infoLink: String?,
        val type: Int,
        val data: List<Any>?
)

data class HeroPosition(
        val x: Double,
        val y: Double,
        val dx: Double,
        val dy: Double
)

data class Permissions(
        @SerializedName("can_participate_in_pvp") val canParticipateInPvp: Boolean,
        @SerializedName("can_repair_building") val canRepairBuilding: Boolean
)

data class Might(
        val value: Double,
        @SerializedName("crit_chance") val criticalChance: Double,
        @SerializedName("pvp_effectiveness_bonus") val pvpEffectivenessBonus: Double,
        @SerializedName("politics_power") val politicsPower: Double
)