package org.thetale.api.enumerations

enum class SkillAvailability constructor(val code: String) {

    PLAYERS("только для игроков"),
    MONSTERS("только для монстров"),
    ALL("для всех")

}
