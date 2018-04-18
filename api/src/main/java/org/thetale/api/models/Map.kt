package org.thetale.api.models

import com.google.gson.annotations.SerializedName
import kotlin.collections.Map

data class MapRegion(val turn: Int, val region: Region)

data class Region(
        @SerializedName("format_version") val formatVersion: String,
        @SerializedName("map_version") val mapVersion: String,
        val width: Int,
        val height: Int,
        @SerializedName("draw_info") val drawInfo: List<List<List<List<Int>>>>,
        val places: Map<Int, Place>,
        val roads: Map<Int, Road>)

data class Place(
        val name: String,
        val race: Int,
        val pos: Pos,
        val id: Int,
        val size: Int
)

data class Pos(
        val x: Int,
        val y: Int
)

data class Road(
        @SerializedName("point_1_id") val point1Id: Int,
        @SerializedName("point_2_id") val point2Id: Int,
        val id: Int,
        val exists: Boolean,
        val length: Double,
        val path: String
)