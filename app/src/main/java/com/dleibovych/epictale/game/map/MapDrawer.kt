package com.dleibovych.epictale.game.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.dictionary.MapStyle
import com.dleibovych.epictale.api.model.SpriteTileInfo
import com.dleibovych.epictale.api.response.MapTerrainResponse
import com.dleibovych.epictale.util.PreferencesManager
import org.thetale.api.models.Hero
import org.thetale.api.models.Region

import kotlin.coroutines.experimental.suspendCoroutine

class MapDrawer {

    /**
     * Gets an appropriate map sprite
     * @param mapStyle desired map style
     */
    suspend fun getMapSprite(context: Context, mapStyle: MapStyle): Bitmap = suspendCoroutine {
        val url = Uri.parse(String.format(MAP_SPRITE_URL,
                PreferencesManager.getStaticContentUrl(), mapStyle.path))
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

    /**
     * Returns a bitmap to draw a map on
     * @param mapInfo map information
     * @return empty bitmap of necessary size
     */
    fun getMapBitmap(region: Region): Bitmap {
        currentSizeDenominator = 1
        while (true) {
            val size = (region.width * MAP_TILE_SIZE / currentSizeDenominator *
                    (region.height * MAP_TILE_SIZE / currentSizeDenominator) * 4).toLong()
            if (size < TheTaleApplication.freeMemory * 0.9) {
                val bitmapConfig: Bitmap.Config = if (currentSizeDenominator > 1) {
                    Bitmap.Config.RGB_565
                } else {
                    Bitmap.Config.ARGB_8888
                }

                return Bitmap.createBitmap(region.width * MAP_TILE_SIZE / currentSizeDenominator,
                        region.height * MAP_TILE_SIZE / currentSizeDenominator, bitmapConfig)
            }
            currentSizeDenominator *= 2
        }
    }

    /**
     * Synchronously draws a base layer of the map (terrain, places, buildings)
     * @param canvas a canvas for the map bitmap to draw layer on
     * @param mapInfo map information
     * @param sprite map sprite
     */
    fun drawBaseLayer(canvas: Canvas, region: Region, sprite: Bitmap) {
        val tileRect = Rect(0, 0, MAP_TILE_SIZE, MAP_TILE_SIZE)
        val drawInfo = region.drawInfo
        val rowsCount = drawInfo.size
        for (i in 0 until rowsCount) {
            val row = drawInfo[i]
            val cellsCount = row.size
            for (j in 0 until cellsCount) {
                val dst = Rect(
                        j * MAP_TILE_SIZE / currentSizeDenominator, i * MAP_TILE_SIZE / currentSizeDenominator,
                        (j + 1) * MAP_TILE_SIZE / currentSizeDenominator, (i + 1) * MAP_TILE_SIZE / currentSizeDenominator)
                for (tile in row[j]) {
                    val spriteTile = getSpriteTile(tile[0], tile[1])
                    val rotation = tile[1]
                    if (rotation == 0) {
                        val src = Rect(
                                spriteTile.x, spriteTile.y,
                                spriteTile.x + spriteTile.size, spriteTile.y + spriteTile
                                .size)
                        canvas.drawBitmap(sprite, src, dst, null)
                    } else {
                        val rotationMatrix = Matrix()
                        rotationMatrix.setRotate(rotation.toFloat())
                        val rotatingTileBitmap = Bitmap.createBitmap(
                                sprite,
                                spriteTile.x, spriteTile.y,
                                MAP_TILE_SIZE, MAP_TILE_SIZE,
                                rotationMatrix, true)
                        canvas.drawBitmap(rotatingTileBitmap, tileRect, dst, null)
                    }
                }
            }
        }
    }

    /**
     * Synchronously draws a layer with place names
     * todo hacky calculation of sizes & positions
     * @param canvas a canvas for the map bitmap to draw layer on
     * @param mapInfo map information
     */
    fun drawPlaceNamesLayer(canvas: Canvas, region: Region) {
        if (currentSizeDenominator > 2) {
            return
        }

        for (placeInfo in region.places.values) {
            var textSizeDenominator = currentSizeDenominator.toFloat()
            if (PLACE_TEXT_SIZE / currentSizeDenominator < PLACE_TEXT_SIZE_MIN) {
                textSizeDenominator = PLACE_TEXT_SIZE / PLACE_TEXT_SIZE_MIN
            }

            val text = String.format(PLACE_CAPTION, placeInfo.size, placeInfo.name)

            val textPaint = Paint()
            textPaint.textSize = PLACE_TEXT_SIZE / textSizeDenominator
            textPaint.color = TheTaleApplication.context!!.resources.getColor(R.color.map_place_name)

            val textRect = Rect()
            textPaint.getTextBounds(text, 0, text.length, textRect)

            val x = ((placeInfo.pos.x + 0.5f) * MAP_TILE_SIZE + PLACE_TEXT_X_SHIFT) / currentSizeDenominator - (textRect.right - textRect.left) / 2.0f
            val y = ((placeInfo.pos.y + 1.0f) * MAP_TILE_SIZE + PLACE_TEXT_Y_SHIFT) / currentSizeDenominator

            val backgroundPaint = Paint()
            backgroundPaint.color = TheTaleApplication.context!!.resources.getColor(R.color.map_place_name_background)

            canvas.drawRect(
                    x + textRect.left - PLACE_TEXT_BACKGROUND_PADDING * 2f * (currentSizeDenominator / textSizeDenominator),
                    y + textRect.top - PLACE_TEXT_BACKGROUND_PADDING,
                    x + textRect.right.toFloat() + PLACE_TEXT_BACKGROUND_PADDING * 4f * (currentSizeDenominator / textSizeDenominator),
                    y + textRect.bottom.toFloat() + PLACE_TEXT_BACKGROUND_PADDING,
                    backgroundPaint)
            canvas.drawText(text, 0, text.length, x, y, textPaint)
        }
    }

    /**
     * Synchronously draws a layer with hero
     * @param canvas a canvas for the map bitmap to draw layer on
     * @param heroInfo hero information
     * @param sprite map sprite
     */
    fun drawHeroLayer(canvas: Canvas, hero: Hero, sprite: Bitmap) {
        val dst = Rect(
                (hero.position.x * MAP_TILE_SIZE / currentSizeDenominator).toInt(),
                ((hero.position.y * MAP_TILE_SIZE + HERO_SPRITE_SHIFT_Y) / currentSizeDenominator).toInt(),
                ((hero.position.x + 1.0) * MAP_TILE_SIZE / currentSizeDenominator).toInt(),
                (((hero.position.y + 1.0) * MAP_TILE_SIZE + HERO_SPRITE_SHIFT_Y) / currentSizeDenominator).toInt())
        val tile = getSpriteTile(hero.sprite)
        if (hero.position.dx >= 0) {
            val src = Rect(
                    tile.x, tile.y,
                    tile.x + tile.size, tile.y + tile.size)
            canvas.drawBitmap(sprite, src, dst, null)
        } else {
            val mirroringMatrix = Matrix()
            mirroringMatrix.setScale(-1f, 1f)
            val mirroredTileBitmap = Bitmap.createBitmap(
                    sprite,
                    tile.x, tile.y,
                    MAP_TILE_SIZE, MAP_TILE_SIZE,
                    mirroringMatrix, true)
            canvas.drawBitmap(mirroredTileBitmap, Rect(0, 0, MAP_TILE_SIZE, MAP_TILE_SIZE), dst, null)
        }
    }

    fun drawModificationLayer(canvas: Canvas, region: Region,
                              terrainInfo: MapTerrainResponse, modification: MapModification) {
        MapModification.init(modification, region)
        for (y in 0 until region.height) {
            val row = terrainInfo.cells[y]
            for (x in 0 until region.width) {
                modification.modifyCell(canvas, x, y, row[x])
            }
        }
    }

    @JvmOverloads
    fun getSpriteTile(spriteId: Int, rotation: Int = 0): SpriteTileInfo {
        return SpriteTileInfo(MAP_SPRITE_OFFSET_X[spriteId], MAP_SPRITE_OFFSET_Y[spriteId], rotation, MAP_TILE_SIZE)
    }

    companion object {

        private const val MAP_SPRITE_URL = "http:%s%s"
        const val MAP_TILE_SIZE = 32
        private const val HERO_SPRITE_SHIFT_Y = -12

        private const val PLACE_CAPTION = "(%d) %s"
        private const val PLACE_TEXT_SIZE = 12f
        private const val PLACE_TEXT_SIZE_MIN = 8f
        private const val PLACE_TEXT_X_SHIFT = -2f
        private const val PLACE_TEXT_Y_SHIFT = 12f
        private const val PLACE_TEXT_BACKGROUND_PADDING = 2f

        private val MAP_SPRITE_OFFSET_X = intArrayOf(0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 0, 64, 128, 192, 256, 128, 96, 32, 0, 32, 96, 128, 160, 0, 64, 0, 32, 64, 96, 160, 224, 352, 320, 288, 192, 256, 192, 224, 256, 288, 192, 224, 64, 352, 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 0, 32, 64, 96, 128, 160, 192, 224, 96, 32, 160, 64, 128, 0, 0, 32, 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 0, 32, 64, 96, 128, 160, 192, 224, 256, 288)
        private val MAP_SPRITE_OFFSET_Y = intArrayOf(256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 64, 64, 64, 64, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 64, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 64, 64, 64, 32, 192, 192, 192, 192, 192, 192, 192, 192, 192, 192, 192, 192, 224, 224, 224, 224, 224, 224, 224, 224, 96, 96, 96, 96, 96, 96, 288, 288, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 160, 160, 160, 160, 160, 160, 160, 160, 160, 160)

        var currentSizeDenominator = 1
            private set

    }
}
