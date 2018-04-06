package com.dleibovych.epictale.login.steps.thirdparty

interface LoginThirdPartyView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun hideError()

    fun openThirdPartyLink(url: String)
}