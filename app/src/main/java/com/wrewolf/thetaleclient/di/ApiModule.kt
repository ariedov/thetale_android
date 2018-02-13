package com.wrewolf.thetaleclient.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.thetale.api.ClientBuilder
import org.thetale.api.TheTaleService
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun httpClient(): OkHttpClient {

        return OkHttpClient.Builder()
                .addInterceptor {
                    var request = it.request()
                    val url = request.url()
                            .newBuilder()
                            .addQueryParameter("app_version", "value")
                            .build()
                    request = request.newBuilder().url(url).build()
                    it.proceed(request)
                }
                .build()
    }

    @Provides
    @Singleton
    fun theTaleApi(client: OkHttpClient): TheTaleService {
        val builder = ClientBuilder()
        return builder.build(client)
    }
}