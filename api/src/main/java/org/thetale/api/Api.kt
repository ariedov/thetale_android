package org.thetale.api

import kotlinx.coroutines.experimental.Deferred
import org.thetale.api.error.ResponseException
import org.thetale.api.models.Response

suspend fun <T> Deferred<Response<T>>.readDataOrThrow(): T? {
    val response = await()
    if (response.isError()) {
        throw ResponseException(response.error, response.errors)
    }
    return response.data
}