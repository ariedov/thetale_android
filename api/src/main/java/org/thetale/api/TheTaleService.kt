package org.thetale.api

import kotlinx.coroutines.experimental.Deferred
import org.thetale.api.models.*
import retrofit2.http.*

interface TheTaleService {

    @GET("api/info")
    fun info(@Query("api_version") appVersion: String = "1.0"): Deferred<Response<AppInfo>>

    @FormUrlEncoded
    @POST("accounts/auth/api/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("remember") remember: Boolean = true,
            @Query("api_version") apiClient: String = "1.0"): Deferred<Response<AuthInfo>>

    @FormUrlEncoded
    @POST("/accounts/third-party/tokens/api/request-authorisation")
    fun login(
            @Field("application_name") applicationName: String,
            @Field("application_info") applicationInfo: String,
            @Field("application_description") applicationDescription: String,
            @Query("api_version") apiVersion: String = "1.0"): Deferred<Response<ThirdPartyLink>>

    @GET("/accounts/third-party/tokens/api/authorisation-state")
    fun authorizationState(@Query("api_version") apiVersion: String = "1.0"): Deferred<Response<ThirdPartyStatus>>

    @POST("/accounts/auth/api/logout")
    fun logout(@Query("api_version") apiClient: String = "1.0"): Deferred<Response<Empty>>

    @GET("/game/api/info")
    fun gameInfo(
            @Query("client_turns") clientTurns: String = "",
            @Query("api_version") apiVersion: String = "1.9"): Deferred<Response<GameInfo>>

    @GET("/game/map/api/region-versions")
    fun mapRegionVersions(
            @Query("api_version") apiVersion: String = "0.1"): Deferred<Response<MapRegionVersions>>

    @GET("/game/map/api/region")
    fun mapRegion(
            @Query("turn") turn: Int,
            @Query("api_version") apiVersion: String = "0.1"): Deferred<Response<MapRegion>>

    @POST("/game/abilities/{ability}/api/use")
    fun useAbility(@Path("ability") ability: String,
                   @Query("api_version") apiVersion: String = "1.0"): Deferred<Response<Empty>>

    @GET("/accounts/{account}/api/show")
    fun getAccount(@Path("account") account: Int,
                   @Query("api_version") apiVersion: String = "1.0"): Deferred<Response<AccountInfo>>

    @POST("/game/quests/api/choose")
    fun chooseQuestAction(@Query("option_uid") option: String,
                          @Query("api_version") apiVersion: String = "1.0"): Deferred<Response<Empty>>

    @GET("/game/api/diary")
    fun diary(@Query("api_version") apiVersion: String = "1.0"): Deferred<Response<DiaryInfo>>
}