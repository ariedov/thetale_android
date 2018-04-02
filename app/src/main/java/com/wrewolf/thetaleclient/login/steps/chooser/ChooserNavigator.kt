package com.wrewolf.thetaleclient.login.steps.chooser

interface ChooserNavigator {

    fun openThirdPartyLogin(url: String)

    fun openCredentialsLogin()
}