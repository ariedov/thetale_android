package com.wrewolf.thetaleclient.login

import io.reactivex.disposables.CompositeDisposable
import org.thetale.api.TheTaleService
import org.thetale.api.common.ApiRequest
import org.thetale.api.common.RequestState
import javax.inject.Inject

interface LoginNavigation {

    fun startRegistration()

    fun proceedToGame()
}

interface LoginView {

    fun setLoading()

    fun enableLogin()

    fun enableRetry()

    fun showInfoError()

    fun showLoginError()

    fun showLoginMethodChooser()

    fun showLoginEmail()

}

class LoginPresenter @Inject constructor(private val service: TheTaleService) {

    lateinit var view: LoginView
    lateinit var navigator: LoginNavigation

    private val disposables = CompositeDisposable()
    private val infoRequest = ApiRequest(service.info())

    fun checkAppInfo() {
        disposables.add(infoRequest.state.subscribe {
            when (it) {
                RequestState.Idle -> {
                    infoRequest.execute()
                }
                RequestState.Loading -> {
                    view.setLoading()
                }
                is RequestState.Done<*> -> {
                    view.enableLogin()
                }
                is RequestState.Error -> {
                    view.enableRetry()
                    view.showInfoError()
                }
            }
        })
    }

    fun chooseLoginWithCredentials() {
        view.showLoginEmail()
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        val request = ApiRequest(service.login(email, password))
        disposables.add(request.state.subscribe {
            when (it) {
                RequestState.Loading -> {
                    view.setLoading()
                }
                is RequestState.Done<*> -> {
                    navigator.proceedToGame()
                }
                is RequestState.Error -> {
                    view.showLoginError()
                }
            }
        })
        request.execute()
    }

    fun loginFromSite() {
    }

    fun registerAccount() {

    }

    fun remindPassword() {
    }

    fun dispose() {
        disposables.dispose()
    }
}