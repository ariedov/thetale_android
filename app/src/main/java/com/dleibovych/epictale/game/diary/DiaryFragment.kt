package com.dleibovych.epictale.game.diary

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dleibovych.epictale.R
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.UiUtils
import kotlinx.android.synthetic.main.fragment_diary.*

import javax.inject.Inject

import org.thetale.api.models.DiaryInfo

class DiaryFragment : Fragment(), DiaryView {

    @Inject
    lateinit var presenter: DiaryPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)

        presenter.view = this

        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error.onRetryClick(View.OnClickListener {
            presenter.loadDiary()
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

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
        }
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        diaryContainer.visibility = View.GONE
        error.visibility = View.GONE
    }

    override fun showDiary(info: DiaryInfo) {
        diaryContainer.visibility = View.VISIBLE
        progress.visibility = View.GONE
        error.visibility = View.GONE

        diaryContainer!!.removeAllViews()
        for (message in info.messages) {
            val diaryEntryView = layoutInflater!!.inflate(R.layout.item_diary, diaryContainer, false)
            UiUtils.setText(
                    diaryEntryView.findViewById(R.id.diary_place),
                    message.position)
            UiUtils.setText(
                    diaryEntryView.findViewById(R.id.diary_time),
                    String.format("%s %s", message.gameTime, message.gameDate))
            UiUtils.setText(
                    diaryEntryView.findViewById(R.id.diary_text),
                    message.message)
            diaryContainer!!.addView(diaryEntryView)
        }
    }

    override fun showError() {
        diaryContainer.visibility = View.GONE
        progress.visibility = View.GONE
        error.visibility = View.VISIBLE

        error.setErrorText(getString(R.string.common_error))
    }
}
