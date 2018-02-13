package org.thetale.api.common

import org.thetale.api.models.Response

sealed class RequestState {
    object Idle: RequestState()
    object Loading: RequestState()
    data class Done<T>(val data: Response<T>): RequestState()
    data class Error(val error: Throwable) : RequestState()
}