package com.dleibovych.epictale.game.gameinfo.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater

import android.widget.TableLayout
import com.dleibovych.epictale.R
import kotlinx.android.synthetic.main.layout_hero_stats.view.*
import org.thetale.api.models.AccountInfo

class StatsView @kotlin.jvm.JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : TableLayout(context, attrs) {

    init {
        isStretchAllColumns = true
        LayoutInflater.from(context).inflate(R.layout.layout_hero_stats, this)
    }

    fun bind(account: AccountInfo) {

        val hero = account.hero

        physicalPower.text = hero.secondary.power[0].toString()
        magicalPower.text = hero.secondary.power[1].toString()
        money.text = hero.base.money.toString()
        might.text = hero.might.value.toString()

        if (account.isOwn) {
            energyHolder.visibility = VISIBLE
            energy.text = account.energy.toString()
        } else {
            energyHolder.visibility = GONE
        }
    }
}