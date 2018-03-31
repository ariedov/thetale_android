package com.wrewolf.thetaleclient.login

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.thetale.api.*
import org.thetale.api.models.*
import javax.inject.Inject

interface LoginNavigation {

    fun startRegistration()

    fun proceedToGame()

    fun openThirdPartyAuth(link: String)
}


class LoginPresenter @Inject constructor(private val service: TheTaleService) {

    lateinit var view: LoginView
    lateinit var navigator: LoginNavigation

    private var appInfoDeferred: Deferred<Result<AppInfo>>? = null
    private var loginDeferred: Deferred<Result<AuthInfo>>? = null
    private var thirdPartyDeferred: Deferred<Result<ThirdPartyLink>>? = null
    private var thirdPartyStatusDeferred: Deferred<Result<ThirdPartyStatus>>? = null

    fun start() {
        checkAppInfo()
    }

    private fun checkAppInfo() {
        appInfoDeferred = async(UI) {
            service.info().getResult()
                    .onSuccess { view.showChooser() }
                    .onError { view.showInitError() }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {

        loginDeferred = async {
            service.login(email, password).getResult()
                    .onSuccess {
                        if (it.isError()) {

                        } else {
                            navigator.proceedToGame()
                        }
                    }
                    .onError {  }
        }
    }

    fun thirdPartyLogin(appName: String, appInfo: String, appDescription: String) {
        thirdPartyDeferred = async {
            service.login(appName, appInfo, appDescription).getResult()
                    .onSuccess { navigator.openThirdPartyAuth(it.data!!.authorizationPage) }
                    .onError {  }
        }
    }

    fun thirdPartyAuthStatus() {
        thirdPartyStatusDeferred = async {
            service.authorizationState().getResult()
                    .onSuccess {
                        if (it.isError()) {

                        } else {
                            navigator.proceedToGame()
                        }
                    }
                    .onError {  }
        }
    }

    fun dispose() {
        appInfoDeferred?.cancel()
        loginDeferred?.cancel()
        thirdPartyDeferred?.cancel()
    }
}