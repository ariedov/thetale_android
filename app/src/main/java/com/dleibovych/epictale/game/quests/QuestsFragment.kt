package com.dleibovych.epictale.game.quests

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.dleibovych.epictale.R
import com.dleibovych.epictale.api.ApiResponseCallback
import com.dleibovych.epictale.api.request.QuestChoiceRequest
import com.dleibovych.epictale.api.response.CommonResponse
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.RequestUtils
import com.dleibovych.epictale.util.UiUtils

import java.net.CookieManager
import java.util.HashMap

import javax.inject.Inject

import okhttp3.OkHttpClient
import org.thetale.api.enumerations.QuestType
import org.thetale.api.models.GameInfo

class QuestsFragment : Fragment(), QuestsView {

    @Inject lateinit var client: OkHttpClient
    @Inject lateinit var manager: CookieManager
    @Inject lateinit var presenter: QuestsPresenter

    private var rootView: View? = null

    private var container: ViewGroup? = null

    private val actorNames = HashMap<TextView, Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)


        rootView = layoutInflater!!.inflate(R.layout.fragment_quests, container, false)

        this.container = rootView!!.findViewById(R.id.quests_container)

        return rootView
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
                if (questStep.experience > 0 && questStep.power == 0) {
                    rewards = String.format(" (%s)", getString(R.string.quest_reward_experience, questStep.experience))
                } else if (questStep.experience == 0 && questStep.power > 0) {
                    rewards = String.format(" (%s)", getString(R.string.quest_reward_power, questStep.power))
                } else if (questStep.experience > 0 && questStep.power > 0) {
                    rewards = String.format(" (%s, %s)",
                            getString(R.string.quest_reward_experience, questStep.experience),
                            getString(R.string.quest_reward_power, questStep.power))
                } else {
                    rewards = null
                }
                if (TextUtils.isEmpty(rewards)) {
                    questNameView.text = questStep.name
                } else {
                    val rewardsString = SpannableString(rewards)
                    rewardsString.setSpan(ForegroundColorSpan(resources.getColor(R.color.game_additional_info)),
                            0, rewardsString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    questNameView.text = TextUtils.concat(questStep.name, rewardsString)
                }

//                (questStepView.findViewById<View>(R.id.quest_icon) as ImageView).setImageResource(questStep.type.drawableResId)
//
//                val actorsContainer = questStepView.findViewById<View>(R.id.quest_actors_container) as ViewGroup
//                loop@ for (actor in questStep.actors) {
//                    val actorView = layoutInflater!!.inflate(R.layout.item_text, actorsContainer, false)
//
//                    val actorTextView = actorView.findViewById<View>(R.id.item_text_content) as TextView
//                    val actorText: CharSequence
//                    val actorName = SpannableString(actor.name)
//                    actorName.setSpan(StyleSpan(Typeface.BOLD), 0, actorName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//                    if (actor.type == null) continue
//                    when (actor.type) {
//                        QuestActorType.PERSON -> {
//                            if (actor.personInfo == null) continue@loop
//                            actorText = TextUtils.concat(actorName, ": ", actor.personInfo.name)
//                            actorNames[actorTextView] = actor.personInfo.placeId
//                        }
//
//                        QuestActorType.PLACE -> {
//                            if (actor.placeInfo == null) continue@loop
//                            actorText = TextUtils.concat(actorName, ": ", actor.placeInfo.name)
//                        }
//
//                        QuestActorType.SPENDING -> {
//                            if (actor.spendingInfo == null) continue@loop
//                            actorText = TextUtils.concat(actorName, ": ", actor.spendingInfo.goal)
//                        }
//
//                        else -> actorText = actorName
//                    }
//                    actorTextView.text = actorText
//                    actorTextView.setOnClickListener { DialogUtils.showQuestActorDialog(fragmentManager, actor) }
//
//                    actorsContainer.addView(actorView)
//                }
//
                if (questStep.type !== QuestType.SPENDING.code && j == questStepsCount - 1) {
                    UiUtils.setText(questStepView.findViewById(R.id.quest_action), questStep.action)
                    UiUtils.setText(questStepView.findViewById(R.id.quest_current_choice), questStep.choice)
                }

//                if (info.account!!.isOwn) {
//                    val choicesContainer = questStepView.findViewById<View>(R.id.quest_choices_container) as ViewGroup
//                    val choiceProgress = questStepView.findViewById<View>(R.id.quest_choice_progress)
//                    val choiceError = questStepView.findViewById<View>(R.id.quest_choice_error) as TextView
//                    for (choice in questStep.choiceAlternatives) {
//                        val choiceView = layoutInflater!!.inflate(R.layout.item_quest_choice, choicesContainer, false)
//
//                        val choiceDescription = SpannableString(choice.description)
//                        choiceDescription.setSpan(ForegroundColorSpan(resources.getColor(R.color.common_link)),
//                                0, choice.description.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                        val choiceTextView = choiceView.findViewById<View>(R.id.quest_choice) as TextView
//                        choiceTextView.text = TextUtils.concat(getString(R.string.quest_choice_part), choiceDescription)
//                        choiceTextView.setOnClickListener {
//                            if (PreferencesManager.isConfirmationQuestChoiceEnabled()) {
//                                DialogUtils.showConfirmationDialog(
//                                        childFragmentManager,
//                                        getString(R.string.game_quest_choice),
//                                        Html.fromHtml(getString(R.string.game_quest_choice_confirmation, questStep.name, choice.description))
//                                ) { selectQuestStep(questStepView, choice.id) }
//                            } else {
//                                selectQuestStep(questStepView, choice.id)
//                            }
//                        }
//
//                        choicesContainer.addView(choiceView)
//                    }
//                }

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

    private fun selectQuestStep(questStepView: View, choiceId: String) {
        val choicesContainer = questStepView.findViewById<View>(R.id.quest_choices_container)
        val choiceProgress = questStepView.findViewById<View>(R.id.quest_choice_progress)

        choicesContainer.visibility = View.GONE
        choiceProgress.visibility = View.VISIBLE

        QuestChoiceRequest(client, manager).execute(choiceId, RequestUtils.wrapCallback(object : ApiResponseCallback<CommonResponse> {
            override fun processResponse(response: CommonResponse) {
//                refresh(false)
            }

            override fun processError(response: CommonResponse) {
                choicesContainer.visibility = View.GONE
                choiceProgress.visibility = View.GONE
                UiUtils.setText(questStepView.findViewById(R.id.quest_choice_error), response.errorMessage)
            }
        }, this@QuestsFragment))
    }

    companion object {

        fun create() = QuestsFragment()
    }
}
