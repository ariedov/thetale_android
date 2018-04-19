package org.thetale.api.enumerations

enum class ArtifactType constructor(val code: Int, val typeName: String) {

    JUNK(0, "хлам"),
    MAIN_HAND(1, "основная рука"),
    OFF_HAND(2, "вторая рука"),
    BODY(3, "доспех"),
    AMULET(4, "амулет"),
    HEAD(5, "шлем"),
    CLOAK(6, "плащ"),
    SHOULDERS(7, "наплечники"),
    GLOVES(8, "перчатки"),
    TROUSERS(9, "штаны"),
    BOOTS(10, "обувь"),
    RING(11, "кольцо")

}
