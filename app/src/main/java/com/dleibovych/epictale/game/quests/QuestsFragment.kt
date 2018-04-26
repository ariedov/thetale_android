package com.dleibovych.epictale.game.quests

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.dleibovych.epictale.R
import com.dleibovych.epictale.api.model.QuestActorPlaceInfo
import com.dleibovych.epictale.api.model.QuestActorSpendingInfo
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.UiUtils

import java.util.HashMap

import org.thetale.api.enumerations.QuestType
import org.thetale.api.models.GameInfo
import org.thetale.api.models.QuestActorPersonInfo
import javax.inject.Inject

class QuestsFragment : Fragment(), QuestsView {

    @Inject
    lateinit var presenter: QuestsPresenter

    private var rootView: View? = null

    private var container: ViewGroup? = null

    private val actorNames = HashMap<TextView, Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)

        presenter.view = this

        rootView = layoutInflater!!.inflate(R.layout.fragment_quests, container, false)

        this.container = rootView!!.findViewById(R.id.quests_container)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.view = null
    }

    override fun onStart() {
        super.onStart()

        presenter.start()
    }

    override fun showQuests(info: GameInfo) {
        container!!.removeAllViews()
        actorNames.clear()
        val questLinesCount = info.account!!.hero.quests.quests.size
        for (i in 0 until questLinesCount) {
            val questLine = info.account!!.hero.quests.quests[i]
            val questStepsCount = questLine.line.size
            for (j in 0 until questStepsCount) {
                val questStep = questLine.line[j]
                val questStepView = layoutInflater!!.inflate(R.layout.item_quest, container, false)

                val questNameView = questStepView.findViewById<View>(R.id.quest_name) as TextView
                val rewards: String?
                rewards = if (questStep.experience > 0 && questStep.power == 0) {
                    String.format(" (%s)", getString(R.string.quest_reward_experience, questStep.experience))
                } else if (questStep.experience == 0 && questStep.power > 0) {
                    String.format(" (%s)", getString(R.string.quest_reward_power, questStep.power))
                } else if (questStep.experience > 0 && questStep.power > 0) {
                    String.format(" (%s, %s)",
                            getString(R.string.quest_reward_experience, questStep.experience),
                            getString(R.string.quest_reward_power, questStep.power))
                } else {
                    null
                }

                if (TextUtils.isEmpty(rewards)) {
                    questNameView.text = questStep.name
                } else {
                    val rewardsString = SpannableString(rewards)
                    rewardsString.setSpan(ForegroundColorSpan(resources.getColor(R.color.game_additional_info)),
                            0, rewardsString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    questNameView.text = TextUtils.concat(questStep.name, rewardsString)
                }

                (questStepView.findViewById<View>(R.id.quest_icon) as ImageView)
                        .setImageResource(QuestType.getQuestType(questStep.type).drawableResId)
//
                val actorsContainer = questStepView.findViewById<View>(R.id.quest_actors_container) as ViewGroup
                for (actorInfo in questStep.actors) {
                    val actorView = layoutInflater!!.inflate(R.layout.item_text, actorsContainer, false)

                    val actorTextView = actorView.findViewById<View>(R.id.item_text_content) as TextView
                    val actorText: CharSequence
                    val actorName = SpannableString(actorInfo.name)
                    actorName.setSpan(StyleSpan(Typeface.BOLD), 0, actorName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    val actor = actorInfo.actor
                    when (actor) {
                        is QuestActorPersonInfo -> {
                            actorText = TextUtils.concat(actorName, ": ", actor.name)
                            actorNames[actorTextView] = actor.place
                        }

                        is QuestActorPlaceInfo -> {
                            actorText = TextUtils.concat(actorName, ": ", actor.name)
                        }

                        is QuestActorSpendingInfo -> {
                            actorText = TextUtils.concat(actorName, ": ", actor.goal)
                        }

                        else -> actorText = actorName
                    }
                    actorTextView.text = actorText
//                    actorTextView.setOnClickListener { DialogUtils.showQuestActorDialog(fragmentManager, actor) }

                    actorsContainer.addView(actorView)
                }

                if (questStep.type !== QuestType.SPENDING.code && j == questStepsCount - 1) {
                    UiUtils.setText(questStepView.findViewById(R.id.quest_action), questStep.action)
                    UiUtils.setText(questStepView.findViewById(R.id.quest_current_choice), questStep.choice)
                }

                if (info.account!!.isOwn) {
                    val choicesContainer = questStepView.findViewById<View>(R.id.quest_choices_container) as ViewGroup
                    val choiceProgress = questStepView.findViewById<View>(R.id.quest_choice_progress)
                    val choiceError = questStepView.findViewById<View>(R.id.quest_choice_error) as TextView
                    val choices = questStep.choiceAlternatives
                    if (choices != null) {
                        for (choice in choices) {
                            val choiceView = layoutInflater!!.inflate(R.layout.item_quest_choice, choicesContainer, false)

                            val choiceDescription = SpannableString(choice[1])
                            choiceDescription.setSpan(ForegroundColorSpan(resources.getColor(R.color.common_link)),
                                    0, choice[1].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            val choiceTextView = choiceView.findViewById<View>(R.id.quest_choice) as TextView
                            choiceTextView.text = TextUtils.concat(getString(R.string.quest_choice_part), choiceDescription)
                            choiceTextView.setOnClickListener {
                                presenter.chooseQuestOption(choice[0].toInt())
                            }

                            choicesContainer.addView(choiceView)
                        }
                    }
                }

                container!!.addView(questStepView)
            }

            if (i != questLinesCount - 1) {
                layoutInflater!!.inflate(R.layout.item_quest_delimiter, container, true)
            }
        }

        // add town effectName to quest person actors
        if (actorNames.size > 0) {
            //                    new MapRequest(response.mapVersion).execute(RequestUtils.wrapCallback(new CommonResponseCallback<MapResponse, String>() {
            //                        @Override
            //                        public void processResponse(MapResponse response) {
            //                            for (final Map.Entry<TextView, Integer> actorNameEntry : actorNames.entrySet()) {
            //                                final Spannable placeText = new SpannableString(String.format(" (%s)", response.places.get(actorNameEntry.getValue()).effectName));
            //                                placeText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.game_additional_info)),
            //                                        0, placeText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //
            //                                final TextView actorName = actorNameEntry.getKey();
            //                                actorName.setText(TextUtils.concat(actorName.getText(), placeText));
            //                            }
            //                        }
            //
            //                        @Override
            //                        public void processError(String error) {
            //                            // do nothing
            //                        }
            //                    }, QuestsFragment.this));
        }
    }

    override fun showError() {

    }

    companion object {

        fun create() = QuestsFragment()
    }
}
