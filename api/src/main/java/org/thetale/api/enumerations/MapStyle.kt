package org.thetale.api.enumerations

enum class MapStyle constructor(val path: String, val styleName: String) {

    STANDARD("/game/images/map.png", "Обычная"),
    ALTERNATIVE("/game/images/map_alternative.png", "Альтернативная"),
    WINTER("/game/images/map_winter.png", "Зимняя"),
    LARGE_PIXEL("/game/images/map_large_pixel.png", "Крупный пиксель")

}
