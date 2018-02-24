package com.wrewolf.thetaleclient.login

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.thetale.api.TheTaleService
import org.thetale.api.models.*
import javax.inject.Inject

interface LoginNavigation {

    fun startRegistration()

    fun proceedToGame()

    fun openThirdPartyAuth(link: String)
}

class LoginPresenter @Inject constructor(private val service: TheTaleService) {

    val viewStates: BehaviorRelay<LoginState> = BehaviorRelay.createDefault(LoginState.Initial)

    lateinit var navigator: LoginNavigation

    private val disposables = CompositeDisposable()

    fun checkAppInfo(): Observable<Response<AppInfo>> {
        return Observable.just(LoginState.Loading)
                .doOnNext { viewStates.accept(it) }
                .flatMapSingle { service.info() }
                .doOnNext { viewStates.accept(LoginState.Chooser) }
                .doOnError { viewStates.accept(LoginState.Error()) }
                .onErrorResumeNext(Observable.empty())
    }

    fun loginWithEmailAndPassword(email: String, password: String): Observable<Response<AuthInfo>> {
        return Observable.just(LoginState.Loading)
                .doOnNext { viewStates.accept(it) }
                .flatMapSingle { service.login(email, password) }
                .onErrorResumeNext(Observable.empty())
                .doOnNext {
                    if (it.isError()) {
                        if (it.errors != null) {
                            viewStates.accept(LoginState.CredentialsError(
                                    email, password, it.getEmailError(), it.getPasswordError()))
                        } else {
                            viewStates.accept(LoginState.CredentialsError(
                                    email, password, it.error)
                            )
                        }
                    } else {
                        navigator.proceedToGame()
                    }
                }
                .doOnError { viewStates.accept(LoginState.CredentialsError(email, password)) }
    }

    fun thirdPartyLogin(appName: String, appInfo: String, appDescription: String): Observable<Response<ThirdPartyLink>> {
        return Observable.just(LoginState.Loading)
                .doOnNext { viewStates.accept(it) }
                .flatMapSingle { service.login(appName, appInfo, appDescription) }
                .onErrorResumeNext(Observable.empty())
                .doOnNext {
                    if (it.isError()) {
                        viewStates.accept(LoginState.Error(it.error))
                    } else {
                        viewStates.accept(LoginState.ThirdPartyConfirm)
                        navigator.openThirdPartyAuth(it.data!!.authorizationPage)
                    }
                }
                .doOnError { viewStates.accept(LoginState.ThirdPartyError) }
    }

    fun thirdPartyAuthStatus(): Observable<Response<ThirdPartyStatus>> {
        return Observable.just(LoginState.Loading)
                .doOnNext { viewStates.accept(it) }
                .flatMapSingle { service.authorizationState() }
                .onErrorResumeNext(Observable.empty())
                .doOnNext {
                    when {
                        it.isError() -> viewStates.accept(LoginState.ThirdPartyError)
                        !it.data!!.isAcceptedAuth() -> viewStates.accept(LoginState.ThirdPartyStatusError)
                        else -> navigator.proceedToGame()
                    }
                }
                .doOnError { viewStates.accept(LoginState.ThirdPartyError) }
    }

    fun dispose() {
        disposables.dispose()
    }
}