package com.dleibovych.epictale.game.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import org.thetale.api.TheTaleService
import org.thetale.api.enumerations.MapStyle
import java.io.IOException
import kotlin.coroutines.experimental.suspendCoroutine

private const val MAP_SPRITE_URL = "https://%s%s"

class MapSpriteProvider(private val client: OkHttpClient,
                        private val service: TheTaleService) {

    fun getMapSprite(mapStyle: MapStyle) = async(UI) {
        val info = service.info().await()
        return@async loadSprite(mapStyle, info.data!!.staticContent)
    }

    private suspend fun loadSprite(mapStyle: MapStyle, staticUrl: String): Bitmap = suspendCoroutine {
        val request = Request.Builder()
                .url(String.format(MAP_SPRITE_URL, staticUrl, mapStyle.path))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val bytes = response?.body()?.bytes()
                if (bytes != null) {
                    it.resume(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                } else {
                    it.resumeWithException(RuntimeException("empty body"))
                }
            }
        })
    }
}