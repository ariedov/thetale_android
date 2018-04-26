package org.thetale.api.models

import com.google.gson.annotations.SerializedName

class QuestActors: ArrayList<QuestActor<*>>()

data class QuestActor<T>(
        val name: String,
        val type: Int,
        val actor: T
)

data class QuestActorPlace(
        val id: Int,
        val name: String
)

data class QuestActorPersonInfo(
        val id: Int,
        val name: String,
        val race: Int,
        val gender: Int,
        val profession: Int,
        @SerializedName("mastery_verbose") val masteryVerbose: String,
        val place: Int
)

data class QuestActorSpendingInfo(
        val goal: String
)
