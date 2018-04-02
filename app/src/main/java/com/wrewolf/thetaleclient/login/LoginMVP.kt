package com.wrewolf.thetaleclient.login

import com.wrewolf.thetaleclient.PresenterState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.*
import org.thetale.api.models.isAcceptedAuth
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

    private var appInfoDeferred: Job? = null
    private var loginDeferred: Job? = null
    private var thirdPartyDeferred: Job? = null
    private var thirdPartyStatusDeferred: Job? = null

    fun start() {
        state.apply()
    }

    private fun checkAppInfo() {
        appInfoDeferred = launch(UI) {
            try {
                service.info().call()
                state.apply { view.showChooser() }
            } catch (e: Exception) {
                state.apply { view.showInitError() }
            }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        loginDeferred = launch(UI) {
            service.login(email, password).call()
            navigator.proceedToGame()
            state.clear()
        }
    }

    fun thirdPartyAuthStatus() {
        thirdPartyStatusDeferred = launch(UI) {
            try {
                val status = service.authorizationState().call()
                if (status.isAcceptedAuth()) {
                    navigator.proceedToGame()
                    state.clear()
                } else {
                    state.apply { view.showThirdPartyStatusError() }
                }
            } catch (e: Exception) {
                state.apply { view.showThirdPartyStatusError() }
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