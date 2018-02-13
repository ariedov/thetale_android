package org.thetale.api

import io.reactivex.Observable
import org.thetale.api.models.AppInfo
import org.thetale.api.models.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TheTaleService {

    @GET("api/info") fun info(@Query("api_client") apiClient: String): Observable<Response<AppInfo>>
}