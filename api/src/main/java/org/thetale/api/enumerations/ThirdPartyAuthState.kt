package org.thetale.api.enumerations

enum class ThirdPartyAuthState constructor(val code: Int, val description: String) {

    NOT_REQUESTED(0, "Авторизация не запрашивалась"),
    NOT_DECIDED(1, "Пользователь ещё не принял решение"),
    SUCCESS(2, "Авторизация прошла успешно"),
    REJECT(3, "В авторизации отказано")
}
