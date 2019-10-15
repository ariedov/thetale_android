package com.dleibovych.epictale.game.quests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.dleibovych.epictale.R
import com.dleibovych.epictale.game.di.GameComponentProvider
import kotlinx.android.synthetic.main.fragment_quests.*

import org.thetale.api.models.GameInfo
import javax.inject.Inject

class QuestsFragment : Fragment(), QuestsView {

    @Inject
    lateinit var presenter: QuestsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)

        presenter.view = this

        return layoutInflater.inflate(R.layout.fragment_quests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentQuest.setChoiceListener { presenter.chooseQuestOption(it) }

        error.onRetryClick(View.OnClickListener {
            presenter.loadQuests()
        })
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

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        content.visibility = View.GONE
    }

    override fun showQuests(info: GameInfo) {
        content.visibility = View.VISIBLE
        error.visibility = View.GONE
        progress.visibility = View.GONE


        val questLine = info.account!!.hero.quests.quests.last()
        val quest = questLine.line.last()
        currentQuest.bind(quest)
    }

    override fun showError() {
        error.visibility = View.VISIBLE
        progress.visibility = View.GONE
        content.visibility = View.GONE

        error.setErrorText(getString(R.string.common_error))
    }

    override fun showQuestActionProgress() {
        currentQuest.showQuestProgress()
    }

    override fun showQuestActionError() {
        currentQuest.hideQuestProgress()
        Toast.makeText(activity!!, R.string.common_error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
        }
    }

    companion object {

        fun create() = QuestsFragment()
    }
}
