package com.dleibovych.epictale.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import android.widget.TextView

import com.dleibovych.epictale.DataViewMode
import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.ApiResponseCallback
import com.dleibovych.epictale.api.cache.prerequisite.InfoPrerequisiteRequest
import com.dleibovych.epictale.api.cache.prerequisite.PrerequisiteRequest
import com.dleibovych.epictale.api.dictionary.Action
import com.dleibovych.epictale.api.dictionary.ArtifactEffect
import com.dleibovych.epictale.api.dictionary.Habit
import com.dleibovych.epictale.api.dictionary.HeroAction
import com.dleibovych.epictale.api.model.CompanionInfo
import com.dleibovych.epictale.api.request.AbilityUseRequest
import com.dleibovych.epictale.api.request.GameInfoRequest
import com.dleibovych.epictale.api.response.CommonResponse
import com.dleibovych.epictale.api.response.GameInfoResponse
import com.dleibovych.epictale.api.response.InfoResponse
import com.dleibovych.epictale.fragment.dialog.TabbedDialog
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.DialogUtils
import com.dleibovych.epictale.util.GameInfoUtils
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.RequestUtils
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.UiUtils
import com.dleibovych.epictale.util.WebsiteUtils
import com.dleibovych.epictale.util.onscreen.OnscreenPart
import com.dleibovych.epictale.widget.RequestActionView

import java.net.CookieManager
import java.util.Date
import java.util.regex.Pattern

import javax.inject.Inject

import okhttp3.OkHttpClient

class GameInfoFragment : WrapperFragment() {

    @Inject
    lateinit var client: OkHttpClient
    @Inject
    lateinit var manager: CookieManager

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            refresh(false)
            handler.postDelayed(this, REFRESH_TIMEOUT_MILLIS)
        }
    }

    private var rootView: View? = null

    private var textRaceGender: TextView? = null
    private var textLevel: TextView? = null
    private var textName: TextView? = null
    private var textLevelUp: View? = null

    private var progressHealth: ProgressBar? = null
    private var textHealth: TextView? = null
    private var progressExperience: ProgressBar? = null
    private var textExperience: TextView? = null
    private var blockEnergy: View? = null
    private var progressEnergy: ProgressBar? = null
    private var textEnergy: TextView? = null

    private var textPowerPhysical: TextView? = null
    private var textPowerMagical: TextView? = null
    private var textMoney: TextView? = null
    private var textMight: TextView? = null

    private var companionAbsentText: View? = null
    private var companionContainer: View? = null
    private var companionCoherence: TextView? = null
    private var companionName: TextView? = null
    private var progressCompanionHealth: ProgressBar? = null
    private var textCompanionHealth: TextView? = null
    private var progressCompanionExperience: ProgressBar? = null
    private var textCompanionExperience: TextView? = null

    private var progressAction: ProgressBar? = null
    private var progressActionInfo: TextView? = null
    private var textAction: TextView? = null
    private var actionHelp: RequestActionView? = null

    private var journalContainer: ViewGroup? = null

    private var lastJournalTimestamp: Int = 0
    private var lastFightProgress: Double = 0.toDouble()
    private var lastKnownHealth: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as GameComponentProvider).provideGameComponent()?.inject(this)


        rootView = inflater.inflate(R.layout.fragment_game_info, container, false)

        textRaceGender = rootView!!.findViewById(R.id.game_info_race_gender)
        textLevel = rootView!!.findViewById(R.id.game_info_level)
        textName = rootView!!.findViewById(R.id.game_info_name)
        textLevelUp = rootView!!.findViewById(R.id.game_info_lvlup)

        progressHealth = rootView!!.findViewById(R.id.game_info_health_progress)
        textHealth = rootView!!.findViewById(R.id.game_info_health_text)
        progressExperience = rootView!!.findViewById(R.id.game_info_experience_progress)
        textExperience = rootView!!.findViewById(R.id.game_info_experience_text)
        blockEnergy = rootView!!.findViewById(R.id.game_info_energy)
        progressEnergy = rootView!!.findViewById(R.id.game_info_energy_progress)
        textEnergy = rootView!!.findViewById(R.id.game_info_energy_text)

        textPowerPhysical = rootView!!.findViewById(R.id.game_info_power_physical)
        textPowerMagical = rootView!!.findViewById(R.id.game_info_power_magical)
        textMoney = rootView!!.findViewById(R.id.game_info_money)
        textMight = rootView!!.findViewById(R.id.game_info_might)

        companionAbsentText = rootView!!.findViewById(R.id.game_info_companion_absent)
        companionContainer = rootView!!.findViewById(R.id.game_info_companion_container)
        companionCoherence = rootView!!.findViewById(R.id.game_info_companion_coherence)
        companionName = rootView!!.findViewById(R.id.game_info_companion_name)
        progressCompanionHealth = rootView!!.findViewById(R.id.game_info_companion_health_progress)
        textCompanionHealth = rootView!!.findViewById(R.id.game_info_companion_health_text)
        progressCompanionExperience = rootView!!.findViewById(R.id.game_info_companion_experience_progress)
        textCompanionExperience = rootView!!.findViewById(R.id.game_info_companion_experience_text)

        progressAction = rootView!!.findViewById(R.id.game_info_action_progress)
        progressActionInfo = rootView!!.findViewById(R.id.game_info_action_progress_info)
        textAction = rootView!!.findViewById(R.id.game_info_action_text)
        actionHelp = rootView!!.findViewById(R.id.game_help)

        journalContainer = rootView!!.findViewById(R.id.journal_container)

        return wrapView(layoutInflater, rootView)
    }

    override fun onResume() {
        super.onResume()

        actionHelp!!.setActionClickListener {
            AbilityUseRequest(client, manager, Action.HELP).execute(0, RequestUtils.wrapCallback(object : ApiResponseCallback<CommonResponse> {
                override fun processResponse(response: CommonResponse) {
                    actionHelp!!.setMode(RequestActionView.Mode.ACTION)
                    refresh(false)
                }

                override fun processError(response: CommonResponse) {
                    actionHelp!!.setErrorText(response.errorMessage)
                }
            }, this@GameInfoFragment))
        }

        handler.postDelayed(refreshRunnable, REFRESH_TIMEOUT_MILLIS)
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(refreshRunnable)
    }

    override fun refresh(isGlobal: Boolean) {
        super.refresh(isGlobal)

        if (isGlobal) {
            lastJournalTimestamp = 0
            lastFightProgress = 0.0
            lastKnownHealth = 0
        }

        val callback = RequestUtils.wrapCallback(object : ApiResponseCallback<GameInfoResponse> {
            override fun processResponse(gameInfoResponse: GameInfoResponse) {
                if (lastKnownHealth == 0) {
                    lastKnownHealth = Math.round((450.0 + 50.0 * gameInfoResponse.account.hero.basicInfo.level) / 4.0).toInt()
                }

                textRaceGender!!.text = String.format("%s-%s",
                        gameInfoResponse.account.hero.basicInfo.race.getName(),
                        gameInfoResponse.account.hero.basicInfo.gender.getName())
                textLevel!!.text = gameInfoResponse.account.hero.basicInfo.level.toString()
                textName!!.text = gameInfoResponse.account.hero.basicInfo.name
                if (gameInfoResponse.account.hero.basicInfo.destinyPoints > 0) {
                    textLevelUp!!.setOnClickListener { v ->
                        if (gameInfoResponse.account.isOwnInfo) {
                            DialogUtils.showConfirmationDialog(
                                    childFragmentManager,
                                    getString(R.string.game_lvlup_dialog_title),
                                    getString(R.string.game_lvlup_dialog_message, gameInfoResponse.account.hero.basicInfo.destinyPoints),
                                    getString(R.string.drawer_title_site), {
                                startActivity(UiUtils.getOpenLinkIntent(String.format(
                                        WebsiteUtils.URL_PROFILE_HERO, gameInfoResponse.account.hero.id)))
                            }, null, null, null)
                        } else {
                            DialogUtils.showMessageDialog(
                                    childFragmentManager,
                                    getString(R.string.game_lvlup_dialog_title),
                                    getString(R.string.game_lvlup_dialog_message_foreign,
                                            gameInfoResponse.account.hero.basicInfo.destinyPoints))
                        }
                    }
                    textLevelUp!!.visibility = View.VISIBLE
                } else {
                    textLevelUp!!.visibility = View.GONE
                }

                val additionalInfoStringBuilder = SpannableStringBuilder()
                val lastVisit = Date(gameInfoResponse.account.lastVisitTime.toLong() * 1000)
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_account_id),
                        gameInfoResponse.account.accountId.toString()))
                        .append("\n")
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_last_visit),
                        String.format("%s %s",
                                DateFormat.getDateFormat(TheTaleApplication.context).format(lastVisit),
                                DateFormat.getTimeFormat(TheTaleApplication.context).format(lastVisit))))
                        .append("\n")
                if (gameInfoResponse.account.isOwnInfo) {
                    additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                            getString(R.string.game_additional_info_new_messages),
                            gameInfoResponse.account.newMessagesCount.toString()))
                            .append("\n")
                }
                additionalInfoStringBuilder.append("\n")
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_honor),
                        String.format("%s (%.2f)",
                                gameInfoResponse.account.hero.habits[Habit.HONOR]!!.description,
                                gameInfoResponse.account.hero.habits[Habit.HONOR]!!.value)))
                        .append("\n")
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_peacefulness),
                        String.format("%s (%.2f)",
                                gameInfoResponse.account.hero.habits[Habit.PEACEFULNESS]!!.description,
                                gameInfoResponse.account.hero.habits[Habit.PEACEFULNESS]!!.value)))
                        .append("\n")
                additionalInfoStringBuilder.append("\n")
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_destiny_points),
                        gameInfoResponse.account.hero.basicInfo.destinyPoints.toString()))
                        .append("\n")
                if (gameInfoResponse.account.isOwnInfo && gameInfoResponse.account.hero.cards != null) {
                    additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                            getString(R.string.game_help_to_next_card),
                            getString(
                                    R.string.game_help_progress_to_next_card,
                                    gameInfoResponse.account.hero.cards.cardHelpCurrent,
                                    gameInfoResponse.account.hero.cards.cardHelpBarrier)))
                            .append("\n")
                }
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_move_speed),
                        gameInfoResponse.account.hero.basicInfo.moveSpeed.toString()))
                        .append("\n")
                additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                        getString(R.string.game_additional_info_initiative),
                        gameInfoResponse.account.hero.basicInfo.initiative.toString()))
                textName!!.setOnClickListener {
                    DialogUtils.showMessageDialog(childFragmentManager,
                            getString(R.string.game_additional_info),
                            additionalInfoStringBuilder)
                }

                progressHealth!!.max = gameInfoResponse.account.hero.basicInfo.healthMax
                progressHealth!!.progress = gameInfoResponse.account.hero.basicInfo.healthCurrent
                textHealth!!.text = String.format("%d/%d",
                        gameInfoResponse.account.hero.basicInfo.healthCurrent,
                        gameInfoResponse.account.hero.basicInfo.healthMax)

                progressExperience!!.max = gameInfoResponse.account.hero.basicInfo.experienceForNextLevel
                progressExperience!!.progress = gameInfoResponse.account.hero.basicInfo.experienceCurrent
                textExperience!!.text = String.format("%d/%d",
                        gameInfoResponse.account.hero.basicInfo.experienceCurrent,
                        gameInfoResponse.account.hero.basicInfo.experienceForNextLevel)

                blockEnergy!!.visibility = if (gameInfoResponse.account.isOwnInfo) View.VISIBLE else View.GONE
                if (gameInfoResponse.account.isOwnInfo) {
                    val energy = gameInfoResponse.account.hero.energy
                    progressEnergy!!.max = energy.max
                    // https://code.google.com/p/android/issues/detail?id=12945
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        progressEnergy!!.progress = 0
                    }
                    progressEnergy!!.progress = energy.current
                    textEnergy!!.text = GameInfoUtils.getEnergyString(energy)
                }

                textPowerPhysical!!.text = gameInfoResponse.account.hero.basicInfo.powerPhysical.toString()
                textPowerMagical!!.text = gameInfoResponse.account.hero.basicInfo.powerMagical.toString()
                textMoney!!.text = gameInfoResponse.account.hero.basicInfo.money.toString()

                val mightInfo = gameInfoResponse.account.hero.might
                textMight!!.text = mightInfo.value.toString()
                textMight!!.setOnClickListener { DialogUtils.showMightDialog(fragmentManager, mightInfo) }

                val companion = gameInfoResponse.account.hero.companionInfo
                if (companion == null) {
                    companionContainer!!.visibility = View.GONE
                    companionAbsentText!!.visibility = View.VISIBLE
                } else {
                    companionAbsentText!!.visibility = View.GONE
                    companionContainer!!.visibility = View.VISIBLE

                    companionName!!.text = companion.name
                    companionCoherence!!.text = companion.coherence.toString()

                    progressCompanionHealth!!.max = companion.healthMax
                    progressCompanionHealth!!.progress = companion.healthCurrent
                    textCompanionHealth!!.text = String.format("%d/%d",
                            companion.healthCurrent, companion.healthMax)

                    progressCompanionExperience!!.max = companion.experienceForNextLevel
                    progressCompanionExperience!!.progress = companion.experienceCurrent
                    textCompanionExperience!!.text = String.format("%d/%d",
                            companion.experienceCurrent, companion.experienceForNextLevel)

                    if (companion.species == null) {
                        companionName!!.setTextColor(resources.getColor(R.color.common_text))
                    } else {
                        companionName!!.setTextColor(resources.getColor(R.color.common_link))
                        companionName!!.setOnClickListener { v ->
                            DialogUtils.showTabbedDialog(childFragmentManager,
                                    companion.name, CompanionTabsAdapter(companion, companion.coherence))
                        }
                    }
                }

                val action = gameInfoResponse.account.hero.action
                progressAction!!.max = 1000
                progressAction!!.progress = (1000 * action.completion).toInt()
                textAction!!.text = GameInfoUtils.getActionString(activity, action)

                val journal = gameInfoResponse.account.hero.journal
                val journalSize = journal.size
                journalContainer!!.removeAllViews()
                for (i in journalSize - 1 downTo 0) {
                    val journalEntry = journal[i]
                    val journalEntryView = layoutInflater!!.inflate(R.layout.item_journal, journalContainer, false)
                    (journalEntryView.findViewById<View>(R.id.journal_time) as TextView).text = journalEntry.time
                    (journalEntryView.findViewById<View>(R.id.journal_text) as TextView).text = journalEntry.text
                    journalContainer!!.addView(journalEntryView)
                }

                if (!isGlobal
                        && TheTaleApplication.onscreenStateWatcher!!.isOnscreen(OnscreenPart.GAME_INFO)
                        && PreferencesManager.isJournalReadAloudEnabled()) {
                    for (i in 0 until journalSize) {
                        val journalEntry = journal[i]
                        if (journalEntry.timestamp > lastJournalTimestamp) {
                            TextToSpeechUtils.speak(journalEntry.text)
                        }
                    }
                }

                if (journalSize > 0) {
                    if (journalSize > 1 && journal[journalSize - 2].timestamp == lastJournalTimestamp && action.type == HeroAction.BATTLE) {
                        val pattern = Pattern.compile("(\\d+)")
                        val matcher = pattern.matcher(journal[journalSize - 1].text)
                        if (matcher.find()) {
                            val number = matcher.group(1)
                            if (!matcher.find()) {
                                val amount = Integer.decode(number)!!
                                val difference = Math.abs(action.completion - lastFightProgress)
                                if (difference != 0.0) {
                                    lastKnownHealth = Math.round(amount / difference).toInt()
                                }
                            }
                        }
                    }

                    lastJournalTimestamp = journal[journalSize - 1].timestamp
                    if (action.type == HeroAction.BATTLE) {
                        lastFightProgress = action.completion
                    } else {
                        lastFightProgress = 0.0
                    }
                } else {
                    lastJournalTimestamp = 0
                    lastFightProgress = 0.0
                }

                when (action.type) {
                    HeroAction.BATTLE -> if (lastKnownHealth != 0) {
                        setProgressActionInfo(String.format("%d / %d HP",
                                Math.round(lastKnownHealth * (1 - action.completion)), lastKnownHealth))
                    } else {
                        setProgressActionInfo(null)
                    }

                    HeroAction.IDLE -> setProgressActionInfo(getActionTimeString(Math.ceil(
                            (1 - action.completion)
                                    * Math.pow(0.75, GameInfoUtils.getArtifactEffectCount(gameInfoResponse.account.hero, ArtifactEffect.ACTIVENESS).toDouble())
                                    * gameInfoResponse.account.hero.basicInfo.level.toDouble()).toLong()))

                    HeroAction.RESURRECTION -> setProgressActionInfo(getActionTimeString(Math.ceil(
                            (1 - action.completion)
                                    * 3.0 * Math.pow(0.75, GameInfoUtils.getArtifactEffectCount(gameInfoResponse.account.hero, ArtifactEffect.ACTIVENESS).toDouble())
                                    * gameInfoResponse.account.hero.basicInfo.level.toDouble()).toLong()))

                    HeroAction.REST -> InfoPrerequisiteRequest(client, manager, {
                        val turnDelta = PreferencesManager.getTurnDelta()
                        var timeRest = Math.round(
                                (gameInfoResponse.account.hero.basicInfo.healthMax - gameInfoResponse.account.hero.basicInfo.healthCurrent) / (// amount of health restored each turn
                                        gameInfoResponse.account.hero.basicInfo.healthMax / 30.0 * Math.pow(2.0, GameInfoUtils.getArtifactEffectCount(gameInfoResponse.account.hero, ArtifactEffect.ENDURANCE).toDouble())) * turnDelta)
                        timeRest = Math.round(timeRest.toDouble() / turnDelta) * turnDelta
                        setProgressActionInfo(getActionTimeApproximateString(if (timeRest < turnDelta) turnDelta.toLong() else timeRest))
                    }, object : PrerequisiteRequest.ErrorCallback<InfoResponse>() {
                        override fun processError(response: InfoResponse) {
                            setProgressActionInfo(null)
                        }
                    }, this@GameInfoFragment).execute()

                    else -> setProgressActionInfo(null)
                }

                if (gameInfoResponse.account.isOwnInfo) {
                    actionHelp!!.visibility = View.VISIBLE
                    actionHelp!!.isEnabled = false
                    InfoPrerequisiteRequest(client, manager, {
                        if (GameInfoUtils.isEnoughEnergy(gameInfoResponse.account.hero.energy, PreferencesManager.getAbilityCost(Action.HELP))) {
                            actionHelp!!.isEnabled = true
                        }
                    }, object : PrerequisiteRequest.ErrorCallback<InfoResponse>() {
                        override fun processError(response: InfoResponse) {
                            actionHelp!!.setErrorText(response.errorMessage)
                            actionHelp!!.setMode(RequestActionView.Mode.ERROR)
                        }
                    }, this@GameInfoFragment).execute()
                } else {
                    actionHelp!!.visibility = View.GONE
                }

                setMode(DataViewMode.DATA)
            }

            override fun processError(response: GameInfoResponse) {
                setError(response.errorMessage)
            }
        }, this)

        val watchingAccountId = PreferencesManager.getWatchingAccountId()
        if (watchingAccountId == 0) {
            GameInfoRequest(client, manager, true).execute(callback, false)
        } else {
            GameInfoRequest(client, manager, true).execute(watchingAccountId, callback, false)
        }
    }

    private fun setProgressActionInfo(info: CharSequence?) {
        if (TextUtils.isEmpty(info)) {
            progressActionInfo!!.visibility = View.GONE
            UiUtils.setHeight(progressAction, resources.getDimension(R.dimen.game_info_bar_height).toInt())
        } else {
            progressActionInfo!!.text = info
            progressActionInfo!!.visibility = View.VISIBLE
            progressActionInfo!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height = progressActionInfo!!.height
                    if (height > 0) {
                        if (isAdded) {
                            UiUtils.setHeight(progressAction,
                                    height + 2 * resources.getDimension(R.dimen.game_info_bar_padding).toInt())
                        }
                        UiUtils.removeGlobalLayoutListener(progressActionInfo!!, this)
                    }
                }
            })
        }
    }

    private fun getActionTimeString(minutes: Long): String? {
        if (minutes < 0) {
            return null
        }

        val hours = minutes / 60
        return if (hours > 0) {
            getString(R.string.game_action_time, hours, minutes % 60)
        } else {
            getString(R.string.game_action_time_short, minutes)
        }
    }

    private fun getActionTimeApproximateString(seconds: Long): String? {
        if (seconds < 0) {
            return null
        }

        val minutes = seconds / 60
        return if (minutes > 0) {
            getString(R.string.game_action_time_approximate, minutes, seconds % 60)
        } else {
            getString(R.string.game_action_time_approximate_short, seconds)
        }
    }

    override fun onOffscreen() {
        super.onOffscreen()
        TheTaleApplication.onscreenStateWatcher?.onscreenStateChange(OnscreenPart.GAME_INFO, false)

        TextToSpeechUtils.pause()
    }

    override fun onOnscreen() {
        super.onOnscreen()
        TheTaleApplication.onscreenStateWatcher?.onscreenStateChange(OnscreenPart.GAME_INFO, true)

        TheTaleApplication.notificationManager?.clearNotifications()
    }

    private inner class CompanionTabsAdapter internal constructor(private val companion: CompanionInfo, private val coherence: Int) : TabbedDialog.TabbedDialogTabsAdapter() {

        override fun getCount(): Int {
            return 3
        }

        override fun getItem(i: Int): Fragment? {
            when (i) {
                0 -> return CompanionParamsFragment.newInstance(companion)

                1 -> return CompanionFeaturesFragment.newInstance(companion, coherence)

                2 -> return CompanionDescriptionFragment.newInstance(companion)

                else -> return null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return getString(R.string.game_companion_tab_params)

                1 -> return getString(R.string.game_companion_tab_features)

                2 -> return getString(R.string.game_companion_tab_description)

                else -> return null
            }
        }

    }

    companion object {

        private val REFRESH_TIMEOUT_MILLIS: Long = 10000 // 10 s

        public fun create() = GameInfoFragment()
    }

}
