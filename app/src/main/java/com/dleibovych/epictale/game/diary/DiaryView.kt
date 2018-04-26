package com.dleibovych.epictale.game.diary

import org.thetale.api.models.DiaryInfo
import org.thetale.api.models.GameInfo

interface DiaryView {

    fun showDiary(info: DiaryInfo)

    fun showError()
}