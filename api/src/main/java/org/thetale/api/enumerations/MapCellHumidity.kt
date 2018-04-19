package org.thetale.api.enumerations

enum class MapCellHumidity constructor(val code: String) {

    EXTREMELY_DRY("ужасно сухо"),
    VERY_DRY("очень сухо"),
    DRY("сухо"),
    DRYISH("пониженная влажность"),
    AVERAGE("умеренная влажность"),
    WETTISH("повышенная влажность"),
    WET("влажно"),
    VERY_WET("очень влажно"),
    FOG("туман"),
    THICK_FOG("сильный туман")

}
