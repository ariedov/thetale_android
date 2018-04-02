package com.wrewolf.thetaleclient.login.steps.chooser

internal interface ChooserNavigator {

    fun openThirdPartyLogin(url: String)

    fun openCredentialsLogin()
}