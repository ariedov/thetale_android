package com.wrewolf.thetaleclient.login

import com.wrewolf.thetaleclient.PresenterState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.*
import org.thetale.api.models.isAcceptedAuth
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val service: TheTaleService,
                                         private val navigation: LoginNavigation) {

    private val state = PresenterState { checkAppInfo() }

    private var appInfoJob: Job? = null
    private var loginJob: Job? = null
    private var thirdPartyJob: Job? = null
    private var thirdPartyStatusJob: Job? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    private fun checkAppInfo() {
        appInfoJob = launch(UI) {
            try {
                service.info().call()
                navigation.showChooser()
            } catch (e: Exception) {
            }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        loginJob = launch(UI) {
            service.login(email, password).call()
//            navigator.proceedToGame()
            state.clear()
        }
    }

    fun thirdPartyAuthStatus() {
        thirdPartyStatusJob = launch(UI) {
            try {
                val status = service.authorizationState().call()
                if (status.isAcceptedAuth()) {
//                    navigator.proceedToGame()
                    state.clear()
                } else {
//                    view.showThirdPartyStatusError() }
                }
            } catch (e: Exception) {
//                state.apply { view.showThirdPartyStatusError() }
            }
        }
    }

    fun dispose() {
        appInfoJob?.cancel()
        loginJob?.cancel()
        thirdPartyJob?.cancel()
        thirdPartyStatusJob?.cancel()
    }
}