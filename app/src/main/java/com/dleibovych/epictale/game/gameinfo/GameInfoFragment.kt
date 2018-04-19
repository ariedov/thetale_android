package com.dleibovych.epictale.game.gameinfo

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
import org.thetale.api.enumerations.Action
import org.thetale.api.enumerations.ArtifactEffect
import org.thetale.api.enumerations.Habit
import org.thetale.api.enumerations.HeroAction
import com.dleibovych.epictale.api.request.AbilityUseRequest
import com.dleibovych.epictale.api.response.CommonResponse
import com.dleibovych.epictale.api.response.InfoResponse
import com.dleibovych.epictale.fragment.WrapperFragment
import com.dleibovych.epictale.fragment.dialog.TabbedDialog
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.GameInfoUtils
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.RequestUtils
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.UiUtils
import com.dleibovych.epictale.util.onscreen.OnscreenPart
import com.dleibovych.epictale.widget.RequestActionView
import kotlinx.android.synthetic.main.fragment_game_info.*

import java.net.CookieManager
import java.util.Date
import java.util.regex.Pattern

import javax.inject.Inject

import okhttp3.OkHttpClient
import org.thetale.api.models.CompanionInfo
import org.thetale.api.models.GameInfo

class GameInfoFragment : WrapperFragment(), GameInfoView {

    @Inject lateinit var client: OkHttpClient
    @Inject lateinit var manager: CookieManager
    @Inject lateinit var presenter: GameInfoPresenter

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            refresh(false)
            handler.postDelayed(this, REFRESH_TIMEOUT_MILLIS)
        }
    }

    private var rootView: View? = null

    private var blockEnergy: View? = null
    private var textEnergy: TextView? = null
    private var textPowerPhysical: TextView? = null
    private var textPowerMagical: TextView? = null
    private var textMoney: TextView? = null
    private var textMight: TextView? = null

    private var companionAbsentText: View? = null

    private var progressAction: ProgressBar? = null
    private var progressActionInfo: TextView? = null
    private var textAction: TextView? = null
    private var actionHelp: RequestActionView? = null

    private var journalContainer: ViewGroup? = null

    private var lastJournalTimestamp: Double = 0.toDouble()
    private var lastFightProgress: Double = 0.toDouble()
    private var lastKnownHealth: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as GameComponentProvider).provideGameComponent()?.inject(this)

        presenter.view = this

        rootView = inflater.inflate(R.layout.fragment_game_info, container, false)

        blockEnergy = rootView!!.findViewById(R.id.game_info_energy)
        textEnergy = rootView!!.findViewById(R.id.game_info_energy_text)

        textPowerPhysical = rootView!!.findViewById(R.id.game_info_power_physical)
        textPowerMagical = rootView!!.findViewById(R.id.game_info_power_magical)
        textMoney = rootView!!.findViewById(R.id.game_info_money)
        textMight = rootView!!.findViewById(R.id.game_info_might)

        companionAbsentText = rootView!!.findViewById(R.id.game_info_companion_absent)

        progressAction = rootView!!.findViewById(R.id.game_info_action_progress)
        progressActionInfo = rootView!!.findViewById(R.id.game_info_action_progress_info)
        textAction = rootView!!.findViewById(R.id.game_info_action_text)
        actionHelp = rootView!!.findViewById(R.id.game_help)

        journalContainer = rootView!!.findViewById(R.id.journal_container)

        return wrapView(layoutInflater, rootView)
    }

    override fun onStart() {
        super.onStart()

        presenter.start()
    }

    override fun onStop() {
        super.onStop()

        presenter.stop()
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
            lastJournalTimestamp = 0.toDouble()
            lastFightProgress = 0.0
            lastKnownHealth = 0
        }

        presenter.loadGameInfo()
    }

    override fun showGameInfo(info: GameInfo) {
        if (lastKnownHealth == 0) {
            lastKnownHealth = Math.round((450.0 + 50.0 * info.account.hero.base.level) / 4.0).toInt()
        }

        heroInfo.bind(info.account.hero.base)
        val companion = info.account.hero.companion
        if (companion != null) {
            companionAbsentText!!.visibility = View.GONE
            companionInfo.visibility = View.VISIBLE
            companionInfo.bind(companion)
        } else {
            companionAbsentText!!.visibility = View.VISIBLE
            companionInfo.visibility = View.GONE
        }

        val additionalInfoStringBuilder = SpannableStringBuilder()
        val lastVisit = Date(info.account.lastVisit * 1000)
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_account_id),
                info.account.id.toString()))
                .append("\n")
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_last_visit),
                String.format("%s %s",
                        DateFormat.getDateFormat(TheTaleApplication.context).format(lastVisit),
                        DateFormat.getTimeFormat(TheTaleApplication.context).format(lastVisit))))
                .append("\n")
        if (info.account.isOwn) {
            additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                    getString(R.string.game_additional_info_new_messages),
                    info.account.newMessages.toString()))
                    .append("\n")
        }
        additionalInfoStringBuilder.append("\n")
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_honor),
                        info.account.hero.habits[Habit.HONOR.habitName]?.verbose))
                .append("\n")
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_peacefulness),
                        info.account.hero.habits[Habit.PEACEFULNESS.habitName]?.verbose))
                .append("\n")
        additionalInfoStringBuilder.append("\n")
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_destiny_points),
                info.account.hero.base.destinyPoints.toString()))
                .append("\n")
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_move_speed),
                info.account.hero.secondary.moveSpeed.toString()))
                .append("\n")
        additionalInfoStringBuilder.append(UiUtils.getInfoItem(
                getString(R.string.game_additional_info_initiative),
                info.account.hero.secondary.initiative.toString()))

        blockEnergy!!.visibility = if (info.account.isOwn) View.VISIBLE else View.GONE
        if (info.account.isOwn) {
            val energy = info.account.energy
            if (energy != null) {
                textEnergy!!.text = energy.toString()
            }
        }

        textPowerPhysical!!.text = info.account.hero.secondary.power[0].toString()
        textPowerMagical!!.text = info.account.hero.secondary.power[1].toString()
        textMoney!!.text = info.account.hero.base.money.toString()

        val mightInfo = info.account.hero.might
        textMight!!.text = mightInfo.value.toString()
//        textMight!!.setOnClickListener { DialogUtils.showMightDialog(fragmentManager, mightInfo) }

        val action = info.account.hero.action
        progressAction!!.max = 1000
        progressAction!!.progress = (1000 * action.percents).toInt()
        textAction!!.text = GameInfoUtils.getActionString(activity, action)

        val journal = info.account.hero.messages
        val journalSize = journal.size
        journalContainer!!.removeAllViews()
        for (i in journalSize - 1 downTo 0) {
            val journalEntry = journal[i]
            val journalEntryView = layoutInflater!!.inflate(R.layout.item_journal, journalContainer, false)
            (journalEntryView.findViewById<View>(R.id.journal_time) as TextView).text = journalEntry[1].toString()
            (journalEntryView.findViewById<View>(R.id.journal_text) as TextView).text = journalEntry[2].toString()
            journalContainer!!.addView(journalEntryView)
        }

        if (journalSize > 0) {
            if (journalSize > 1 && journal[journalSize - 2][0] == lastJournalTimestamp && action.type == HeroAction.BATTLE.code) {
                val pattern = Pattern.compile("(\\d+)")
                val matcher = pattern.matcher(journal[journalSize - 1][2].toString())
                if (matcher.find()) {
                    val number = matcher.group(1)
                    if (!matcher.find()) {
                        val amount = Integer.decode(number)!!
                        val difference = Math.abs(action.percents - lastFightProgress)
                        if (difference != 0.0) {
                            lastKnownHealth = Math.round(amount / difference).toInt()
                        }
                    }
                }
            }

            lastJournalTimestamp = journal[journalSize - 1][0] as Double
            if (action.type == HeroAction.BATTLE.code) {
                lastFightProgress = action.percents
            } else {
                lastFightProgress = 0.0
            }
        } else {
            lastJournalTimestamp = 0.toDouble()
            lastFightProgress = 0.0
        }

        when (action.type) {
            HeroAction.BATTLE.code -> if (lastKnownHealth != 0) {
                setProgressActionInfo(String.format("%d / %d HP",
                        Math.round(lastKnownHealth * (1 - action.percents)), lastKnownHealth))
            } else {
                setProgressActionInfo(null)
            }

            HeroAction.IDLE.code -> setProgressActionInfo(getActionTimeString(Math.ceil(
                    (1 - action.percents)
                            * Math.pow(0.75, GameInfoUtils.getArtifactEffectCount(info.account.hero, ArtifactEffect.ACTIVENESS).toDouble())
                            * info.account.hero.base.level.toDouble()).toLong()))

            HeroAction.RESURRECTION.code -> setProgressActionInfo(getActionTimeString(Math.ceil(
                    (1 - action.percents)
                            * 3.0 * Math.pow(0.75, GameInfoUtils.getArtifactEffectCount(info.account.hero, ArtifactEffect.ACTIVENESS).toDouble())
                            * info.account.hero.base.level.toDouble()).toLong()))

            HeroAction.REST.code -> InfoPrerequisiteRequest(client, manager, {
                val turnDelta = PreferencesManager.getTurnDelta()
                var timeRest = Math.round(
                        (info.account.hero.base.maxHealth - info.account.hero.base.health) / (// amount of health restored each turn
                                info.account.hero.base.maxHealth / 30.0 * Math.pow(2.0, GameInfoUtils.getArtifactEffectCount(info.account.hero, ArtifactEffect.ENDURANCE).toDouble())) * turnDelta)
                timeRest = Math.round(timeRest.toDouble() / turnDelta) * turnDelta
                setProgressActionInfo(getActionTimeApproximateString(if (timeRest < turnDelta) turnDelta.toLong() else timeRest))
            }, object : PrerequisiteRequest.ErrorCallback<InfoResponse>() {
                override fun processError(response: InfoResponse) {
                    setProgressActionInfo(null)
                }
            }, this@GameInfoFragment).execute()

            else -> setProgressActionInfo(null)
        }

        if (info.account.isOwn) {
            actionHelp!!.visibility = View.VISIBLE
            actionHelp!!.isEnabled = false
            InfoPrerequisiteRequest(client, manager, {
                actionHelp!!.isEnabled = info.account.energy!! > 0
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

    override fun showError() {
        setError(getString(R.string.common_error))
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
//                0 -> return CompanionParamsFragment.newInstance(companion)
//
//                1 -> return CompanionFeaturesFragment.newInstance(companion, coherence)

//                2 -> return CompanionDescriptionFragment.newInstance(companion)

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
