package org.thetale.api.enumerations

enum class ChatMilestone constructor(val code: Int, val title: String) {

    NONE(0, ""),
    HOUR(3600, "около часа назад"),
    TWO_HOURS(7200, "около двух часов назад"),
    SEVERAL_HOURS(10800, "несколько часов назад"),
    HALF_DAY(43200, "полдня назад"),
    YESTERDAY(86400, "вчера"),
    TWO_DAYS(172800, "около двух дней назад"),
    SEVERAL_DAYS(259200, "несколько дней назад"),
    WEEK(604800, "неделю назад"),
    TWO_WEEKS(1209600, "около двух недель назад"),
    SEVERAL_WEEKS(1814400, "несколько недель назад"),
    MONTH(2592000, "месяц назад"),
    TWO_MONTHS(5184000, "около двух месяцев назад"),
    SEVERAL_MONTHS(7776000, "несколько месяцев назад"),
    LONG_TIME(31557600, "давно")

}
