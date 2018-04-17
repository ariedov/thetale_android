package com.dleibovych.epictale.game.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.dictionary.MapCellWindSpeed
import com.dleibovych.epictale.api.model.MapCellTerrainInfo
import org.thetale.api.models.Region

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

sealed class MapModification(val name: String) {

    abstract fun modifyCell(canvas: Canvas, tileX: Int, tileY: Int, cellInfo: MapCellTerrainInfo);

    object None: MapModification("Обычный") {
        override fun modifyCell(canvas: Canvas, tileX: Int, tileY: Int, cellInfo: MapCellTerrainInfo) { }
    }

    object Wind: MapModification("Ветер") {
        override fun modifyCell(canvas: Canvas, tileX: Int, tileY: Int, cellInfo: MapCellTerrainInfo) {
            val x = tileX * MapDrawer.MAP_TILE_SIZE
            val y = tileY * MapDrawer.MAP_TILE_SIZE
            val size = 0.7 * ((cellInfo.windSpeed.ordinal + 1.0) / MapCellWindSpeed.values().size) + 0.25
            val padding = (MapDrawer.MAP_TILE_SIZE * (1.0 - size) / 2.0) as Float

            canvas.drawRect(x.toFloat(), y.toFloat(), (x + MapDrawer.MAP_TILE_SIZE).toFloat(), (y + MapDrawer.MAP_TILE_SIZE).toFloat(), windPaintRect!!)

            val matrix = Matrix()
            matrix.setRotate(cellInfo.windDirection.direction.toFloat())
            val arrow = Bitmap.createBitmap(arrowBitmap, 0, 0, MapDrawer.MAP_TILE_SIZE, MapDrawer.MAP_TILE_SIZE, matrix, true)
            canvas.drawBitmap(arrow,
                    null, RectF(
                    x + padding,
                    y + padding,
                    x + MapDrawer.MAP_TILE_SIZE - padding,
                    y + MapDrawer.MAP_TILE_SIZE - padding), null)
        }
    }

    object Influence: MapModification("Влияние") {
        override fun modifyCell(canvas: Canvas, tileX: Int, tileY: Int, cellInfo: MapCellTerrainInfo) {
            val x = tileX * MapDrawer.MAP_TILE_SIZE
            val y = tileY * MapDrawer.MAP_TILE_SIZE
            canvas.drawRect(x.toFloat(), y.toFloat(), (x + MapDrawer.MAP_TILE_SIZE).toFloat(), (y + MapDrawer.MAP_TILE_SIZE).toFloat(),
                    influencePaint!![if (cellInfo.isWilderness) 0 else cellInfo.neighborPlaceId])
        }
    };


    companion object {

        private var windPaintRect: Paint? = null
        private var arrowBitmap: Bitmap? = null
        private var influencePaint: MutableMap<Int, Paint>? = null

        //    private static int getInfluenceColor(final int index, final int size) {
        //        return Color.HSVToColor(0xc0, // 75% opacity
        //                new float[]{360.0f / size * index, 0.9f, 0.5f});
        //    }

        // http://phrogz.net/css/distinct-colors.html
        private val colors = arrayOf(-0x3fb3f0f1, -0x3f6685e1, -0x3fad3371, -0x3f998c01, -0x3fcce0d4, -0x3f1aa3a4, -0x3f334500, -0x3f47192a, -0x3fd6d634, -0x3f33ffab, -0x3fccd5d7, -0x3f59597b, -0x3fffccd1, -0x3fc1c2b3, -0x3fccffeb, -0x3f33cd00, -0x3f150100, -0x3fa3191a, -0x3fb8c29a, -0x3f0d6e46, -0x3f40798d, -0x3fcfccec, -0x3fd18c8d, -0x3fcfffc0, -0x3f66a390, -0x3f00669a, -0x3f291976, -0x3fffb29a, -0x3f33cc01, -0x3f80e5cd, -0x3f99c2d7, -0x3f997fe6, -0x3f854734, -0x3f72bd5a, -0x3f00ffd5, -0x3f599cdf, -0x3fa199ae, -0x3f994c01, -0x3f30751a, -0x3f99adab, -0x3f006b00, -0x3f8c1a00, -0x3fb8944d, -0x3f758f74, -0x3f73fff4, -0x3fcce200, -0x3fe373e4, -0x3f7a725a, -0x3f0d3d0e, -0x3f335c59, -0x3f1a4076, -0x3feda6dc, -0x3fe5dfc0, -0x3f00cc45, -0x3fa6bd00, -0x3fcc0078, -0x3fe8e08d, -0x3f8ce8ac)

        fun init(mapModification: MapModification, region: Region) {
            when (mapModification) {
                Wind -> {
                    windPaintRect = Paint()
                    windPaintRect!!.color = Color.WHITE

                    val options = BitmapFactory.Options()
                    options.inScaled = false
                    arrowBitmap = BitmapFactory.decodeResource(TheTaleApplication.context!!.resources,
                            R.drawable.ic_arrow, options)
                }

                Influence -> {
                    val size = region.places.size
                    if (size >= colors.size) {
                        throw IllegalStateException("Not enough colors")
                    }
                    influencePaint = HashMap(size + 1)
                    val colorsList = ArrayList<Int>()
                    Collections.addAll(colorsList, *colors)
                    colorsList.shuffle()

                    val wildernessPaint = Paint()
                    wildernessPaint.color = colorsList[0]
                    influencePaint!![0] = wildernessPaint

                    val places = ArrayList(region.places.values)
                    for (i in 0 until size) {
                        val paint = Paint()
                        paint.color = colorsList[i + 1]
                        influencePaint!![places[i].id] = paint
                    }
                }
            }
        }
    }

}
