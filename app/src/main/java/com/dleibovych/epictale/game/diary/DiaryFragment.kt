package com.dleibovych.epictale.game.diary

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dleibovych.epictale.R
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.UiUtils

import javax.inject.Inject

import org.thetale.api.models.DiaryInfo

class DiaryFragment : Fragment(), DiaryView {

    @Inject
    lateinit var presenter: DiaryPresenter

    private var rootView: View? = null

    private var diaryContainer: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)

        presenter.view = this

        rootView = inflater.inflate(R.layout.fragment_diary, container, false)

        diaryContainer = rootView!!.findViewById(R.id.diary_container)

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

    override fun onStop() {
        super.onStop()

        presenter.stop()
    }

    override fun showDiary(info: DiaryInfo) {
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

    }
}
