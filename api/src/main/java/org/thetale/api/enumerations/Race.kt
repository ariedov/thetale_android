package org.thetale.api.enumerations

enum class Race constructor(val code: Int, val raceName: String, val namePlural: String) {

    HUMAN(0, "человек", "люди"),
    ELF(1, "эльф", "эльфы"),
    ORC(2, "орк", "орки"),
    GOBLIN(3, "гоблин", "гоблины"),
    DWARF(4, "дварф", "дварфы")

}
