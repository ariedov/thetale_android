package com.wrewolf.thetaleclient.di

import com.wrewolf.thetaleclient.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.thetale.api.ClientBuilder
import org.thetale.api.TheTaleService
import org.thetale.api.URL
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun cookieManager(): CookieManager {
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(CookieManager())
        }

        val cookieManager = CookieHandler.getDefault() as CookieManager
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        return cookieManager
    }

    @Provides
    @Singleton
    fun cookieJar(cookieManager: CookieManager): CookieJar {
        return JavaNetCookieJar(cookieManager)
    }

    @Provides
    @Singleton
    @IntoSet
    fun appVersionInterceptor(cookieManager: CookieManager): Interceptor {
        return Interceptor {
            var request = it.request()
            val url = request.url()
                    .newBuilder()
                    .addQueryParameter("api_client", "${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}")
                    .build()

            val token = findToken(cookieManager)
            request = request.newBuilder()
                    .apply {
                        if (request.method() == "POST" && token != null) {
                            addHeader(HEADER_CSRF_TOKEN, token)
                            addHeader(HEADER_REFERER, URL)
                        }
                    }
                    .url(url)
                    .build()
            it.proceed(request)
        }
    }

    private fun findToken(cookieManager: CookieManager): String? {
        val predicate: (HttpCookie) -> Boolean = { it.name == COOKIE_CSRF_TOKEN }
        return cookieManager.cookieStore.cookies
                .find(predicate)?.value
    }

    @Provides
    @Singleton
    fun httpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>, cookieJar: CookieJar): OkHttpClient {
        return OkHttpClient.Builder().apply {
            interceptors.forEach {
                addInterceptor(it)
            }
        }
        .cookieJar(cookieJar)
        .build()
    }

    @Provides
    @Singleton
    fun theTaleApi(client: OkHttpClient): TheTaleService {
        val builder = ClientBuilder()
        return builder.build(client)
    }

    companion object {
        private const val COOKIE_CSRF_TOKEN = "csrftoken"
        private const val HEADER_CSRF_TOKEN = "X-CSRFToken"
        private const val HEADER_REFERER = "Referer"

    }
}