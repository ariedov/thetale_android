package com.dleibovych.epictale.game.gameinfo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView

import com.dleibovych.epictale.R
import org.thetale.api.enumerations.Action
import org.thetale.api.enumerations.ArtifactEffect
import org.thetale.api.enumerations.HeroAction
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.GameInfoUtils
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.UiUtils
import com.dleibovych.epictale.widget.RequestActionView
import kotlinx.android.synthetic.main.fragment_game_info.*

import java.util.regex.Pattern

import javax.inject.Inject

import org.thetale.api.models.CompanionInfo
import org.thetale.api.models.GameInfo

class GameInfoFragment : Fragment(), GameInfoView {

    @Inject
    lateinit var presenter: GameInfoPresenter

    private var lastJournalTimestamp: Double = 0.toDouble()
    private var lastFightProgress: Double = 0.toDouble()
    private var lastKnownHealth: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as GameComponentProvider).provideGameComponent()?.inject(this)

        presenter.view = this

        return inflater.inflate(R.layout.fragment_game_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error.onRetryClick(View.OnClickListener { presenter.retry() })

    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.view = null
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

        gameHelp.setActionClickListener { presenter.useAbility(Action.HELP) }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
        }
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        content.visibility = View.GONE
    }

    override fun showGameInfo(info: GameInfo) {
        progress.visibility = View.GONE
        error.visibility = View.GONE
        content.visibility = View.VISIBLE

        val account = info.account!!

        if (lastKnownHealth == 0) {
            lastKnownHealth = Math.round((450.0 + 50.0 * account.hero.base.level) / 4.0).toInt()
        }

        heroInfo.bind(account.hero.base)
        stats.bind(account)
        bindCompanion(account.hero.companion)

        val action = account.hero.action
        gameInfoActionProgress.max = 1000
        gameInfoActionProgress.progress = (1000 * action.percents).toInt()
        gameInfoActionText.text = GameInfoUtils.getActionString(activity, action)

        val journal = account.hero.messages
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
            lastFightProgress = if (action.type == HeroAction.BATTLE.code) action.percents else 0.0
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
                            * Math.pow(0.75, GameInfoUtils.getArtifactEffectCount(account.hero, ArtifactEffect.ACTIVENESS).toDouble())
                            * account.hero.base.level.toDouble()).toLong()))

            HeroAction.RESURRECTION.code -> setProgressActionInfo(getActionTimeString(Math.ceil(
                    (1 - action.percents)
                            * 3.0 * Math.pow(0.75, GameInfoUtils.getArtifactEffectCount(account.hero, ArtifactEffect.ACTIVENESS).toDouble())
                            * account.hero.base.level.toDouble()).toLong()))

            HeroAction.REST.code -> {
                val turnDelta = PreferencesManager.getTurnDelta()
                var timeRest = Math.round(
                        (account.hero.base.maxHealth - account.hero.base.health) / (// amount of health restored each turn
                                account.hero.base.maxHealth / 30.0 * Math.pow(2.0, GameInfoUtils.getArtifactEffectCount(account.hero, ArtifactEffect.ENDURANCE).toDouble())) * turnDelta)
                timeRest = Math.round(timeRest.toDouble() / turnDelta) * turnDelta
                setProgressActionInfo(getActionTimeApproximateString(if (timeRest < turnDelta) turnDelta.toLong() else timeRest))
            }

            else -> setProgressActionInfo(null)
        }

        if (account.isOwn) {
            gameHelp.visibility = View.VISIBLE
            gameHelp.isEnabled = account.energy != null && account.energy!! > 0
            gameHelp.setMode(RequestActionView.Mode.ACTION)
        } else {
            gameHelp.visibility = View.GONE
        }
    }

    private fun bindCompanion(companion: CompanionInfo?) {
        if (companion != null) {
            companionAbsent.visibility = View.GONE
            companionInfo.visibility = View.VISIBLE
            companionInfo.bind(companion)
        } else {
            companionAbsent.visibility = View.VISIBLE
            companionInfo.visibility = View.GONE
        }
    }

    override fun showError() {
        progress.visibility = View.GONE
        content.visibility = View.GONE
        error.visibility = View.VISIBLE

        error.setErrorText(getString(R.string.common_error))
    }

    override fun showAbilityProgress() {
        gameHelp.setMode(RequestActionView.Mode.LOADING)
    }

    override fun showAbilityError() {
        gameHelp.setErrorText(getString(R.string.common_error))
    }

    private fun setProgressActionInfo(info: CharSequence?) {
        if (TextUtils.isEmpty(info)) {
            gameInfoActionProgressInfo.visibility = View.GONE
            UiUtils.setHeight(gameInfoActionProgressInfo, resources.getDimension(R.dimen.game_info_bar_height).toInt())
        } else {
            gameInfoActionProgressInfo.text = info
            gameInfoActionProgressInfo.visibility = View.VISIBLE
            gameInfoActionProgressInfo.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height = gameInfoActionProgressInfo.height
                    if (height > 0) {
                        if (isAdded) {
                            UiUtils.setHeight(gameInfoActionProgressInfo,
                                    height + 2 * resources.getDimension(R.dimen.game_info_bar_padding).toInt())
                        }
                        UiUtils.removeGlobalLayoutListener(gameInfoActionProgressInfo, this)
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

    companion object {

        fun create() = GameInfoFragment()
    }

}
