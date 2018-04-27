package org.thetale.api.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.thetale.api.enumerations.QuestActorType
import org.thetale.api.models.*
import java.lang.reflect.Type

class QuestActorDeserializer : JsonDeserializer<QuestActors> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): QuestActors {
        val questActors = QuestActors()
        if (json != null) {
            for (actorInfo in json as JsonArray) {
                val actorArray = actorInfo as JsonArray
                val name = actorArray[0].asString
                val actorType = actorArray[1].asInt

                questActors.add(QuestActorInfo(name, actorType,
                        readActorInfo(actorType, actorArray[2], context)))
            }
        }
        return questActors
    }

    private fun readActorInfo(actorType: Int, actorInfo: JsonElement, context: JsonDeserializationContext?): QuestActor? {
        return when (actorType) {
            QuestActorType.PERSON.code -> {
                context!!.deserialize<QuestActorPersonInfo>(actorInfo, QuestActorPersonInfo::class.java)
            }

            QuestActorType.PLACE.code -> {
                context!!.deserialize<QuestActorPlace>(actorInfo, QuestActorPersonInfo::class.java)
            }

            QuestActorType.SPENDING.code -> {
                context!!.deserialize<QuestActorSpendingInfo>(actorInfo, QuestActorPersonInfo::class.java)
            }

            else -> null
        }
    }
}