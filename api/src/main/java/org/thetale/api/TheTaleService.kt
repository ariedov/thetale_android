package org.thetale.api

import io.reactivex.Observable
import org.thetale.api.models.AppInfo
import org.thetale.api.models.AuthInfo
import org.thetale.api.models.Response
import retrofit2.http.*

interface TheTaleService {

    @GET("api/info") fun info(@Query("api_version") appVersion: String = "1.0"): Observable<Response<AppInfo>>

    @FormUrlEncoded
    @POST("accounts/auth/api/login") fun login(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("remember") remember: Boolean = false,
            @Query("api_version") apiClient: String = "1.0"): Observable<Response<AuthInfo>>
}