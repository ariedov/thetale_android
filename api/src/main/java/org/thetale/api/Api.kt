package org.thetale.api

import org.thetale.api.models.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import kotlin.coroutines.experimental.suspendCoroutine

suspend fun <T> Call<Response<T>>.getResult(): Result<T> = suspendCoroutine {
    enqueue(object: Callback<Response<T>> {

        override fun onFailure(call: Call<Response<T>>?, t: Throwable?) {
            it.resume(Failure(t))
        }

        override fun onResponse(call: Call<Response<T>>?, response: retrofit2.Response<Response<T>>?) {
            response?.body()?.run { it.resume(Success(this)) }
            response?.errorBody()?.run { it.resume(Failure(HttpException(response))) }
        }
    })
}