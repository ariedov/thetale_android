package org.thetale.api.models

import com.google.gson.annotations.SerializedName

class Response<T> {

    @SerializedName("data") var data: T? = null
    @SerializedName("status") lateinit var status: String
    @SerializedName("error") var error: String? = null
    @SerializedName("errors") var errors: Map<String, List<String>>? = null

    fun isError(): Boolean {
        return status == "error"
    }
}