package com.wrewolf.thetaleclient.login

interface LoginNavigation {

    fun showCheckStatus()

    fun showChooser()

    fun showCredentials()

    fun showThirdParty(link: String)

    fun openApp()
}