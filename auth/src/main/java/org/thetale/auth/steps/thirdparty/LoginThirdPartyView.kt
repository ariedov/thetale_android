package org.thetale.auth.steps.thirdparty

interface LoginThirdPartyView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun hideError()

    fun openThirdPartyLink(url: String)
}