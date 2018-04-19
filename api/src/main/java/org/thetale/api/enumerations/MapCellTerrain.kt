package org.thetale.api.enumerations

enum class MapCellTerrain constructor(val code: String) {

    SWAMP("болото"),
    HIGH_MOUNTAIN("высокие горы"),
    DRY_CRACKED_EARTH("высохшая растрескавшаяся земля"),
    DRY_CRACKED_HILL("высохшие растрескавшиеся холмы"),
    DEEP_WATER("глубокая вода"),
    MUD_HILL("грязевые холмы"),
    MUD("грязь"),
    JUNGLE("джунгли"),
    JUNGLE_HILL("джунгли на холмах"),
    SWAMP_HILL("заболоченные холмы"),
    SWAMP_FOREST("заболоченный лес"),
    SWAMP_FOREST_HILL("заболоченный лес на холмах"),
    GRASS_HILL("зелёные холмы"),
    DECIDUOUS_FOREST("лиственный лес"),
    DECIDUOUS_FOREST_HILL("лиственный лес на холмах"),
    GRASS("луга"),
    SHALLOW_WATER("мелкая вода"),
    DEAD_FOREST("мёртвый лес"),
    DEAD_FOREST_HILL("мёртвый лес на холмах"),
    LOW_MOUNTAIN("низкие горы"),
    SAND_DUNE("песчаные дюны"),
    DESERT("пустыня"),
    DRY_GRASS("сухие луга"),
    CONIFEROUS_FOREST("хвойный лес"),
    CONIFEROUS_FOREST_HILL("хвойный лес на холмах"),
    DRY_GRASS_HILL("холмы с высохшей травой")

}
