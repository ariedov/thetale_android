package com.dleibovych.epictale.login.steps.credentials

import com.dleibovych.epictale.PresenterState
import com.dleibovych.epictale.login.LoginNavigationProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.call
import org.thetale.api.error.ResponseException

class LoginCredentialsPresenter(private val service: TheTaleService,
                                private val navigationProvider: LoginNavigationProvider) {

    private val state = PresenterState { view?.hideProgress() }
    private var loginJob: Job? = null

    var view: LoginCredentialsView? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    fun loginWithCredentials(email: String, password: String) {
        loginJob = launch(UI) {
            try {
                state.apply { view?.showProgress() }
                service.login(email, password).call()

                navigationProvider.navigation?.openApp()

                state.apply { view?.hideProgress() }
            } catch (e: ResponseException) {
                state.apply { view?.showLoginError(e.getEmailError(), e.getPasswordError()) }
            } catch (e: Exception) {
                state.apply { view?.showError() }
            }
        }
    }

    fun navigateToThirdParty() {
        navigationProvider.navigation?.showThirdParty()
    }

    fun dispose() {
        loginJob?.cancel()
    }
}

fun ResponseException.getEmailError(): String? {
    return errors?.get("email")?.get(0)
}

fun ResponseException.getPasswordError(): String? {
    return errors?.get("password")?.get(0)
}