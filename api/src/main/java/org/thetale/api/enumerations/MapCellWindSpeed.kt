package org.thetale.api.enumerations

enum class MapCellWindSpeed constructor(val code: String) {

    CALM("штиль"),
    LIGHT_AIR("тихий ветер"),
    LIGHT_BREEZE("лёгкий ветер"),
    GENTLE_BREEZE("слабый ветер"),
    MODERATE_BREEZE("умеренный ветер"),
    FRESH_BREEZE("свежий ветер"),
    STRONG_BREEZE("сильный ветер"),
    MODERATE_GALE("крепкий ветер"),
    FRESH_GALE("очень крепкий ветер"),
    STRONG_GALE("шторм"),
    WHOLE_GALE("сильный шторм"),
    STORM("жестокий шторм"),
    HURRICANE("ураган")

}
