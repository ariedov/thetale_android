package org.thetale.api.models

import com.google.gson.annotations.SerializedName

class Response<T> {

    @SerializedName("data") var data: T? = null
    @SerializedName("status") lateinit var status: String
    @SerializedName("error") lateinit var error: String
    @SerializedName("errors") lateinit var errors: Map<String, List<String>>
}