package org.thetale.api.models

import com.google.gson.annotations.SerializedName

class QuestActors : ArrayList<QuestActorInfo<*>>()

data class QuestActorInfo<T : QuestActor>(
        val name: String,
        val type: Int,
        val actor: T?
)

sealed class QuestActor

data class QuestActorPlace(
        val id: Int,
        val name: String
) : QuestActor()

data class QuestActorPersonInfo(
        val id: Int,
        val name: String,
        val race: Int,
        val gender: Int,
        val profession: Int,
        @SerializedName("mastery_verbose") val masteryVerbose: String,
        val place: Int
) : QuestActor()

data class QuestActorSpendingInfo(
        val goal: String
) : QuestActor()
