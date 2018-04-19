package org.thetale.api.enumerations

import org.thetale.api.R

enum class CardRarity constructor(val code: Int, val colorResId: Int) {

    COMMON(0, R.color.card_common),
    UNCOMMON(1, R.color.card_uncommon),
    RARE(2, R.color.card_rare),
    EPIC(3, R.color.card_epic),
    LEGENDARY(4, R.color.card_legendary)

}
