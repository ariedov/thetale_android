package com.wrewolf.thetaleclient.login

import com.wrewolf.thetaleclient.PresenterState
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

    private val state = PresenterState { checkAppInfo() }

    private var appInfoDeferred: Deferred<Result<AppInfo>>? = null
    private var loginDeferred: Deferred<Result<AuthInfo>>? = null
    private var thirdPartyDeferred: Deferred<Result<ThirdPartyLink>>? = null
    private var thirdPartyStatusDeferred: Deferred<Result<ThirdPartyStatus>>? = null

    fun start() {
        state.apply()
    }

    private fun checkAppInfo() {
        appInfoDeferred = async(UI) {
            service.info().getResult()
                    .onSuccess { state.apply { view.showChooser() } }
                    .onError { state.apply { view.showInitError() } }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {

        loginDeferred = async(UI) {
            service.login(email, password).getResult()
                    .onSuccess {
                        if (it.isError()) {

                        } else {
                            navigator.proceedToGame()
                            state.clear()
                        }
                    }
                    .onError {
                    }
        }
    }

    fun thirdPartyLogin(appName: String, appInfo: String, appDescription: String) {
        thirdPartyDeferred = async(UI) {
            service.login(appName, appInfo, appDescription).getResult()
                    .onSuccess {
                        navigator.openThirdPartyAuth(it.data!!.authorizationPage)
                        state.apply { view.showThirdParty() }
                    }
        }
    }

    fun thirdPartyAuthStatus() {
        thirdPartyStatusDeferred = async(UI) {
            service.authorizationState().getResult()
                    .onSuccess {
                        navigator.proceedToGame()
                        state.clear()
                    }
                    .onError {
                        state.apply {  view.showThirdPartyStatusError() }
                    }
        }
    }

    fun dispose() {
        appInfoDeferred?.cancel()
        loginDeferred?.cancel()
        thirdPartyDeferred?.cancel()
        thirdPartyStatusDeferred?.cancel()
    }
}