package org.thetale.api

import io.reactivex.Single
import org.thetale.api.models.AppInfoResponse
import retrofit2.http.GET

interface TheTaleService {

    @GET("api/info") fun info(): Single<AppInfoResponse>
}