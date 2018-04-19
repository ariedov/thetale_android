package org.thetale.api.enumerations

enum class QuestActorType constructor(val code: Int, val typeName: String) {

    PERSON(0, "Горожанин"),
    PLACE(1, "Город"),
    SPENDING(2, "Цель накопления")

}
