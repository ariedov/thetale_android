package org.thetale.api

import io.reactivex.Single
import org.thetale.api.models.*
import retrofit2.http.*

interface TheTaleService {

    @GET("api/info")
    fun info(@Query("api_version") appVersion: String = "1.0"): Single<Response<AppInfo>>

    @FormUrlEncoded
    @POST("accounts/auth/api/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("remember") remember: Boolean = false,
            @Query("api_version") apiClient: String = "1.0"): Single<Response<AuthInfo>>

    @FormUrlEncoded
    @POST("/accounts/third-party/tokens/api/request-authorisation")
    fun login(
            @Field("application_name") applicationName: String,
            @Field("application_info") applicationInfo: String,
            @Field("application_description") applicationDescription: String,
            @Query("api_version") apiVersion: String = "1.0"): Single<Response<ThirdPartyLink>>

    @GET("/accounts/third-party/tokens/api/authorisation-state")
    fun authorizationState(@Query("api_version") apiVersion: String = "1.0"): Single<Response<ThirdPartyStatus>>
}