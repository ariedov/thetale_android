package org.thetale.api.models

import com.google.gson.annotations.SerializedName

class ThirdPartyLink(
        @SerializedName("authorisation_page") val authorizationPage: String
)

class ThirdPartyStatus(
        @SerializedName("next_url") val url: String,
        @SerializedName("account_id") val accountId: Int,
        @SerializedName("account_name") val accountName: String,
        @SerializedName("session_expire_at") val expireAt: Long,
        @SerializedName("state") val state: Int
)
