package org.thetale.api.enumerations

enum class MapCellTemperature constructor(val code: String) {

    EXTREMELY_COLD("ужасно холодно"),
    VERY_COLD("очень холодно"),
    COLD("холодно"),
    COOL("прохладно"),
    AVERAGE("умеренная температура"),
    WARM("тепло"),
    HOT("жарко"),
    VERY_HOT("очень жарко"),
    EXTREMELY_HOT("ужасно жарко")

}
