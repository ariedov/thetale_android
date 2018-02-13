package org.thetale.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ClientBuilder {

    fun build(client: OkHttpClient): TheTaleService {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://the-tale.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        return retrofit.create(TheTaleService::class.java)
    }
}