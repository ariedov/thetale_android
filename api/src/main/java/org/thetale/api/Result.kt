package org.thetale.api

import org.thetale.api.models.Response

sealed class Result<out T>
data class Success<out T>(val response: Response<out T>) : Result<T>()
data class Failure(val error: Throwable?) : Result<Nothing>()

fun <T> Result<T>.onSuccess(action: (Response<out T>) -> Unit): Result<T> {
    if (this is Success<T>) action(response)

    return this
}

fun <T> Result<T>.onError(action: (Throwable?) -> Unit): Result<T> {
    if (this is Failure) action(error)

    return this
}