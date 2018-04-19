package org.thetale.api.enumerations

enum class PlaceSpecialization constructor(val code: Int, val specializationName: String) {

    NONE(-1, ""),
    TRADE_CENTER(0, "Торговый центр"),
    MASTERS_CITY(1, "Город мастеров"),
    FORT(2, "Форт"),
    POLITICS_CENTER(3, "Политический центр"),
    POLIS(4, "Полис"),
    RESORT(5, "Курорт"),
    TRANSPORT_NODE(6, "Транспортный узел"),
    LIBERTY_CITY(7, "Вольница"),
    HOLY_CITY(8, "Святой город")

}
