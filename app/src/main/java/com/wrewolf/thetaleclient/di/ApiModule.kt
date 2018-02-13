package com.wrewolf.thetaleclient.di

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.thetale.api.ClientBuilder
import org.thetale.api.TheTaleService
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    @IntoSet
    fun appVersionInterceptor(): Interceptor {
        return Interceptor {
            var request = it.request()
            val url = request.url()
                    .newBuilder()
                    .addQueryParameter("app_version", "value")
                    .build()
            request = request.newBuilder().url(url).build()
            it.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun httpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient {
        return OkHttpClient.Builder().apply {
            interceptors.forEach {
                addInterceptor(it)
            }
        }.build()
    }

    @Provides
    @Singleton
    fun theTaleApi(client: OkHttpClient): TheTaleService {
        val builder = ClientBuilder()
        return builder.build(client)
    }
}