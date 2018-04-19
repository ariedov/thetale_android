package org.thetale.api.enumerations

enum class Action constructor(val code: String) {

    HELP("help"),
    ARENA_GO("arena_pvp_1x1"),
    ARENA_LEAVE("arena_pvp_1x1_leave_queue"),
    ARENA_ACCEPT("arena_pvp_1x1_accept"),
    BUILDING_REPAIR("building_repair"),
    DROP_ITEM("drop_item")

}
