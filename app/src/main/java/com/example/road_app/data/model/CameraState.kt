package com.example.road_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CameraState(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val zoom: Float = 1f
)
