package com.wrewolf.thetaleclient.login

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import org.thetale.api.TheTaleService
import org.thetale.api.common.ApiRequest
import org.thetale.api.common.RequestState
import javax.inject.Inject

interface LoginNavigation {

    fun startRegistration()

    fun proceedToGame()
}

class LoginPresenter @Inject constructor(private val service: TheTaleService) {

    val viewStates: BehaviorRelay<LoginState> = BehaviorRelay.createDefault(LoginState.Initial)

    lateinit var navigator: LoginNavigation

    private val disposables = CompositeDisposable()
    private val infoRequest = ApiRequest(service.info())

    fun checkAppInfo() {
        disposables.add(infoRequest.state.subscribe {
            when (it) {
                RequestState.Loading -> {
                    viewStates.accept(LoginState.Loading)
                }
                is RequestState.Done<*> -> {
                    viewStates.accept(LoginState.Chooser)
                }
                is RequestState.Error -> {
                    viewStates.accept(LoginState.Error())
                }
            }
        })

        infoRequest.execute()
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        val request = ApiRequest(service.login(email, password))
        disposables.add(request.state.subscribe {
            when (it) {
                RequestState.Loading -> {
                    viewStates.accept(LoginState.Loading)
                }
                is RequestState.Done<*> -> {
                    navigator.proceedToGame()
                }
                is RequestState.Error -> {
                    viewStates.accept(LoginState.CredentialsError(email, password))
                }
            }
        })
        request.execute()
    }

    fun dispose() {
        disposables.dispose()
    }
}