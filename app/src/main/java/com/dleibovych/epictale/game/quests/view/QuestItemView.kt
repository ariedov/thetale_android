package com.dleibovych.epictale.game.quests.view

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dleibovych.epictale.R
import com.dleibovych.epictale.api.model.QuestActorSpendingInfo
import kotlinx.android.synthetic.main.item_quest.view.*
import org.thetale.api.enumerations.QuestType
import org.thetale.api.models.*

class QuestItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val inflater: LayoutInflater
    private var choiceListener: ((Int) -> Unit)? = null

    init {
        orientation = VERTICAL
        inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_quest, this)
    }

    fun setChoiceListener(choiceListener: (Int) -> Unit) {
        this.choiceListener = choiceListener
    }

    fun bind(quest: Quest) {
        questName.text = quest.name

        val rewards: String? = readRewards(quest)
        questRewards.visibility = View.GONE
        if (!TextUtils.isEmpty(rewards)) {
            questRewards.text = rewards
            questRewards.visibility = View.VISIBLE
        }

        questIcon.setImageResource(QuestType.getQuestType(quest.type).drawableResId)

        bindActors(quest.actors)
        bindChoices(quest.choiceAlternatives)
    }

    private fun readRewards(quest: Quest): String? = if (quest.experience > 0 && quest.power == 0) {
        resources.getString(R.string.quest_reward_experience, quest.experience)
    } else if (quest.experience == 0 && quest.power > 0) {
        resources.getString(R.string.quest_reward_power, quest.power)
    } else if (quest.experience > 0 && quest.power > 0) {
        String.format("%s, %s",
                resources.getString(R.string.quest_reward_experience, quest.experience),
                resources.getString(R.string.quest_reward_power, quest.power))
    } else {
        null
    }

    private fun bindActors(actors: QuestActors) {
        questActorsContainer.removeAllViews()
        actors.forEach {
            val actorView = inflater.inflate(R.layout.item_text, questActorsContainer, false)

            val actorTextView = actorView.findViewById<View>(R.id.item_text_content) as TextView
            val actorName = SpannableString(it.name)
            actorName.setSpan(StyleSpan(Typeface.BOLD), 0, actorName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            actorTextView.text = TextUtils.concat(actorName, ": ", actorName(it.actor))

            questActorsContainer.addView(actorView)
        }
    }

    private fun actorName(actor: QuestActor?) = when (actor) {
        is QuestActorPersonInfo -> actor.name
        is QuestActorPlace -> actor.name
        is QuestActorSpendingInfo -> actor.goal
        else -> ""
    }

    private fun bindChoices(choices: List<List<String>>?) {
        questChoicesContainer.removeAllViews()
        choices?.forEach { choice ->
            val choiceView = inflater.inflate(R.layout.item_quest_choice, questChoicesContainer, false)

            val choiceDescription = SpannableString(choice[1])
            val foregroundColor = ContextCompat.getColor(context, R.color.common_link)
            choiceDescription.setSpan(ForegroundColorSpan(foregroundColor),
                    0, choice[1].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val choiceTextView = choiceView.findViewById<View>(R.id.quest_choice) as TextView
            choiceTextView.text = TextUtils.concat(resources.getString(R.string.quest_choice_part), choiceDescription)
            choiceTextView.setOnClickListener {
                try {
                    choiceListener?.invoke(choice[0].toInt())
                } catch (e: NumberFormatException) {
                    // ignore
                }
            }

            questChoicesContainer.addView(choiceView)
        }
    }

    fun showQuestProgress() {
        questChoiceProgress.visibility = View.VISIBLE
        questChoicesContainer.visibility = View.GONE
    }

    fun hideQuestProgress() {
        questChoiceProgress.visibility = View.GONE
        questChoicesContainer.visibility = View.VISIBLE
    }
}