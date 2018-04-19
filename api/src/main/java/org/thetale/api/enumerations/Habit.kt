package org.thetale.api.enumerations

enum class Habit constructor(val code: Int, val habitName: String, val description: String) {

    HONOR(0, "honor", "честь"),
    PEACEFULNESS(1, "peacefulness", "миролюбие")
}
