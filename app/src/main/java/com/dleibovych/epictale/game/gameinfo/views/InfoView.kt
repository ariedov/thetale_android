package com.dleibovych.epictale.game.gameinfo.views

import android.content.Context
import android.util.AttributeSet
import android.support.v4.widget.TextViewCompat
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.dleibovych.epictale.R
import kotlinx.android.synthetic.main.layout_account_info.view.*
import org.thetale.api.enumerations.Gender
import org.thetale.api.enumerations.Race
import org.thetale.api.models.Base
import org.thetale.api.models.CompanionInfo

@Suppress("LeakingThis")
abstract class InfoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.layout_account_info, this)
    }
}

class HeroInfoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InfoView(context, attrs, defStyleAttr) {

    fun bind(info: Base) {
        raceGender.text = String.format("%s-%s", Race.findByCode(info.race).raceName, Gender.findByCode(info.gender).genderName)

        level.text = info.level.toString()
        name.text = info.name
        levelUp.visibility = if (info.destinyPoints > 0) VISIBLE else GONE

        healthProgress.max = info.maxHealth
        healthProgress.progress = info.health
        healthText.text = String.format("%d/%d", info.health, info.maxHealth)

        experienceProgress.max = info.experienceToLevel
        experienceProgress.progress = info.experience
        experienceText.text = String.format("%d/%d", info.experience, info.experienceToLevel)
    }
}

class CompanionInfoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InfoView(context, attrs, defStyleAttr) {

    init {
        levelUp.visibility = GONE
        raceGender.visibility = GONE
        TextViewCompat.setTextAppearance(level, R.style.Base_TextAppearance_AppCompat_Medium)
        TextViewCompat.setTextAppearance(name, R.style.Base_TextAppearance_AppCompat_Medium)
    }

    fun bind(companion: CompanionInfo) {
        level.text = companion.coherence.toString()
        name.text = companion.name

        healthProgress.max = companion.maxHealth
        healthProgress.progress = companion.health
        healthText.text = String.format("%d/%d", companion.health, companion.maxHealth)

        experienceProgress.max = companion.experienceToLevel
        experienceProgress.progress = companion.experience
        experienceText.text = String.format("%d/%d", companion.experience, companion.experienceToLevel)
    }
}