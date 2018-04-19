package org.thetale.api.enumerations

enum class Gender constructor(val code: Int, val genderName: String) {

    MALE(0, "мужчина"),
    FEMALE(1, "женщина"),
    IT(2, "оно")

}
