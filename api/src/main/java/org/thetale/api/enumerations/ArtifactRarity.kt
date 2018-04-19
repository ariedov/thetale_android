package org.thetale.api.enumerations

import org.thetale.api.R

enum class ArtifactRarity constructor(val code: Int, val colorResId: Int, val isExceptional: Boolean, val rarityName: String) {

    COMMON(0, R.color.artifact_common, false, "обычный артефакт"),
    RARE(1, R.color.artifact_rare, true, "редкий артефакт"),
    EPIC(2, R.color.artifact_epic, true, "эпический артефакт")

}
