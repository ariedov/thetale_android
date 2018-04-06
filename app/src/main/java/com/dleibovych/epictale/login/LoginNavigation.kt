package com.dleibovych.epictale.login

interface LoginNavigation {

    fun showCheckStatus()

    fun showChooser()

    fun showCredentials()

    fun showThirdParty(link: String)

    fun openApp()
}