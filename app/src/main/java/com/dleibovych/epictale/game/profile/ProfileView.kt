package com.dleibovych.epictale.game.profile

import org.thetale.api.models.AccountInfo

interface ProfileView {

    fun showAccountInfo(info: AccountInfo)

    fun showError()
}