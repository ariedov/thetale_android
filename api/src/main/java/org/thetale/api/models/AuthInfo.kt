package org.thetale.api.models

import com.google.gson.annotations.SerializedName

data class AuthInfo(
    @SerializedName("session_expire_at") val expiresAt: Long,
    @SerializedName("next_url") val nextUrl: String,
    @SerializedName("account_name") val accountName: String,
    @SerializedName("account_id") val accountId: Int
)