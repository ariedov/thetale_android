package org.thetale.api.common

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.thetale.api.error.ApiError
import org.thetale.api.models.Response

class ApiRequest<T>(private val request: Observable<Response<T>>) {

    var state: BehaviorRelay<RequestState> = BehaviorRelay.createDefault(RequestState.Idle)

    private var trigger: BehaviorRelay<Long> = BehaviorRelay.create()

    init {
        trigger
                .doOnNext({ state.accept(RequestState.Loading) })
                .observeOn(Schedulers.io())
                .flatMap { request }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { state.accept(RequestState.Error(it)) }
                .onErrorResumeNext(Observable.empty())
                .doOnNext {
                    if (it.status == "error") {
                        state.accept(RequestState.Error(ApiError("")))
                    } else {
                        state.accept(RequestState.Done(it))
                    }
                }
                .subscribe()
    }

    fun execute() {
        trigger.accept(System.currentTimeMillis())
    }
}