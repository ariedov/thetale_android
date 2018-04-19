package org.thetale.api.enumerations

import java.text.DecimalFormat

enum class RatingItem constructor(val code: String) {

    MIGHT("might") {
        override fun getValue(value: Double): String {
            return DecimalFormat("#.##").format(value)
        }
    },
    BILLS("bills"),
    MAGIC_POWER("magic-power"),
    PHYSICAL_POWER("physic-power"),
    LEVEL("level"),
    PHRASES("phrases"),
    PVP_COUNT("pvp_battles_1x1_number"),
    PVP_VICTORIES("pvp_battles_1x1_victories") {
        override fun getValue(value: Double): String {
            return String.format("%.2f%%", value * 100)
        }
    },
    REFERRALS("referrals_number"),
    ACHIEVEMENT_POINTS("achievements_points"),
    HELP("help_count"),
    GIFTS_RETURNED("gifts_returned"),
    POLITICS_POWER("politics_power") {
        override fun getValue(value: Double): String {
            return String.format("%.2f%%", value * 100)
        }
    };

    open fun getValue(value: Double): String {
        return Math.floor(value).toInt().toString()
    }

}
