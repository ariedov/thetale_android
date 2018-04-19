package org.thetale.api.enumerations

enum class CompanionFeatureType constructor(val typeName: String, val description: String) {

    ROAD("дорожная", "влияет на скорость путешествия героя"),
    BATTLE("боевая", "влияет на битвы"),
    MONEY("денежная", "влияет на деньги и предметы"),
    UNCOMMON("необычная", "имеет особый эффект"),
    PERSISTENT("неизменная", "оказывает постоянный эффект, независимо от других свойств спутника или героя")

}
