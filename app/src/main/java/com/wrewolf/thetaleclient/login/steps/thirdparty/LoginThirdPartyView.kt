package com.wrewolf.thetaleclient.login.steps.thirdparty

interface LoginThirdPartyView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun hideError()

    fun openThirdPartyLink(url: String)
}