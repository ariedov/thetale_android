package com.wrewolf.thetaleclient.login

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.thetale.api.TheTaleService
import org.thetale.api.models.AppInfo
import org.thetale.api.models.Response
import javax.inject.Inject

interface LoginNavigation {

    fun startRegistration()

    fun proceedToGame()
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
                .onErrorResumeNext(Observable.empty())
                .doOnError { viewStates.accept(LoginState.Error()) }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
//        val request = ApiRequest(service.login(email, password))
//        disposables.add(request.state.subscribe {
//            when (it) {
//                RequestState.Loading -> {
//                    viewStates.accept(LoginState.Loading)
//                }
//                is RequestState.Done<*> -> {
//                    navigator.proceedToGame()
//                }
//                is RequestState.Error -> {
//                    viewStates.accept(LoginState.CredentialsError(email, password))
//                }
//            }
//        })
//        request.execute()
    }

    fun dispose() {
        disposables.dispose()
    }
}