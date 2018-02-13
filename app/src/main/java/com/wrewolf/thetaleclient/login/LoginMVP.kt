package com.wrewolf.thetaleclient.login

import org.thetale.api.TheTaleService
import org.thetale.api.common.ApiRequest
import org.thetale.api.common.RequestState
import javax.inject.Inject

interface LoginNavigation {

    fun openApp()
}

interface LoginView {

}

class LoginPresenter @Inject constructor(private val service: TheTaleService) {

    fun checkAppInfo() {
        val request = ApiRequest(service.info(API_VERSION))
        request.state.subscribe {
            when (it) {
                RequestState.Loading -> {}
                is RequestState.Done<*> -> {}
                is RequestState.Error -> {}
            }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {

    }

    companion object {
        const val API_VERSION = "1.0"
    }
}