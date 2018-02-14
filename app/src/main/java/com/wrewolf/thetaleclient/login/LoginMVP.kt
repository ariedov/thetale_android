package com.wrewolf.thetaleclient.login

import org.thetale.api.TheTaleService
import org.thetale.api.common.ApiRequest
import org.thetale.api.common.RequestState
import javax.inject.Inject

interface LoginNavigation {

    fun startRegistration()
}

interface LoginView {

    fun setLoading()

    fun enableLogin()

    fun enableRetry()

    fun showError()

}

class LoginPresenter @Inject constructor(private val service: TheTaleService) {

    lateinit var view: LoginView

    fun checkAppInfo() {
        val request = ApiRequest(service.info(API_VERSION))
        request.state.subscribe {
            when (it) {
                RequestState.Loading -> {
                    view.setLoading()
                }
                is RequestState.Done<*> -> {
                    view.enableLogin()
                }
                is RequestState.Error -> {
                    view.enableRetry()
                    view.showError()
                }
            }
        }
        request.execute()
    }

    fun chooseLoginWithCredentials() {

    }

    fun loginWithEmailAndPassword(email: String, password: String) {

    }

    fun loginFromSite() {

    }

    fun registerAccount() {

    }

    fun remindPassword() {
    }

    companion object {
        const val API_VERSION = "1.0"
    }
}