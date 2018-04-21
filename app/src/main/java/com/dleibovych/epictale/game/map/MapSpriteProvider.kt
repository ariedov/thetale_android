package com.dleibovych.epictale.game.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.thetale.api.TheTaleService
import org.thetale.api.enumerations.MapStyle
import kotlin.coroutines.experimental.suspendCoroutine

private const val MAP_SPRITE_URL = "http:%s%s"

class MapSpriteProvider(private val service: TheTaleService) {

    fun getMapSprite(context: Context, mapStyle: MapStyle) = async(UI) {
        val info = service.info().await()
        return@async loadSprite(context, mapStyle, info.data!!.staticContent)
    }

    private suspend fun loadSprite(context: Context, mapStyle: MapStyle, staticUrl: String): Bitmap = suspendCoroutine {
        val url = Uri.parse(String.format(MAP_SPRITE_URL, staticUrl, mapStyle.path))
        Glide
                .with(context)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        it.resume(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        it.resumeWithException(RuntimeException("bitmap not loaded"))
                    }
                })
    }
}