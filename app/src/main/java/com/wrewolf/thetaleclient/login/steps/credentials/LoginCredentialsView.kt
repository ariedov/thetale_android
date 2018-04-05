package com.wrewolf.thetaleclient.login.steps.credentials

interface LoginCredentialsView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun showLoginError(loginError: String?, passwordError: String?)
}