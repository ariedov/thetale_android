package com.dleibovych.epictale.game.profile

import org.thetale.api.models.AccountInfo

interface ProfileView {

    fun showProgress()

    fun hideProgress()

    fun showAccountInfo(info: AccountInfo)

    fun showError()
}