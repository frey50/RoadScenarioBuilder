package com.example.road_app.util

import androidx.compose.ui.geometry.Offset
import com.example.road_app.data.model.Arrow
import com.example.road_app.data.model.Car
import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.FreeformPath
import com.example.road_app.data.model.RoadSegment
import com.example.road_app.data.model.RoadSegmentKind
import com.example.road_app.data.model.SceneObject
import com.example.road_app.data.model.Sign
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun Offset.toWorld(camera: CameraState): Offset = Offset(
    x = (x - camera.offsetX) / camera.zoom,
    y = (y - camera.offsetY) / camera.zoom
)

fun Offset.toScreen(camera: CameraState): Offset = Offset(
    x = x * camera.zoom + camera.offsetX,
    y = y * camera.zoom + camera.offsetY
)

fun hitTest(worldPoint: Offset, obj: SceneObject): Boolean {
    val dx = worldPoint.x - obj.x
    val dy = worldPoint.y - obj.y
    val rad = Math.toRadians(-obj.rotation.toDouble())
    val c = cos(rad).toFloat()
    val s = sin(rad).toFloat()
    val localX = (dx * c - dy * s) / obj.scale
    val localY = (dx * s + dy * c) / obj.scale

    return when (obj) {
        is RoadSegment -> when (obj.segmentKind) {
            RoadSegmentKind.STRAIGHT -> abs(localX) <= 100f && abs(localY) <= 50f
            RoadSegmentKind.CURVE -> localX >= -100f && localX <= 100f && localY >= -100f && localY <= 100f
            RoadSegmentKind.T_JUNCTION ->
                (abs(localX) <= 50f && abs(localY) <= 100f) ||
                (abs(localX) <= 100f && localY >= -100f && localY <= 0f)
            RoadSegmentKind.FOUR_WAY ->
                (abs(localX) <= 50f && abs(localY) <= 100f) ||
                (abs(localX) <= 100f && abs(localY) <= 50f)
            RoadSegmentKind.ROUNDABOUT -> localX * localX + localY * localY <= 80f * 80f
        }
        is Car -> abs(localX) <= 30f && abs(localY) <= 20f
        is Sign -> localX * localX + localY * localY <= 25f * 25f
        is Arrow -> abs(localX) <= 40f && abs(localY) <= 15f
        is FreeformPath -> false
    }
}

fun angleBetween(a: Offset, b: Offset): Float {
    val dx = b.x - a.x
    val dy = b.y - a.y
    return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
}
