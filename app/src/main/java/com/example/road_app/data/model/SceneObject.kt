package com.example.road_app.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class SceneObject {
    abstract val id: String
    abstract val x: Float
    abstract val y: Float
    abstract val rotation: Float
    abstract val scale: Float
    abstract val layer: Int
}

@Serializable
data class RoadSegment(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val layer: Int = 0,
    val segmentKind: RoadSegmentKind,
    val connectorPoints: List<ConnectorPoint> = emptyList()
) : SceneObject()

@Serializable
data class Car(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val layer: Int = 1,
    val carType: String = "sedan"
) : SceneObject()

@Serializable
data class Sign(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val layer: Int = 1,
    val signType: String = "stop"
) : SceneObject()

@Serializable
data class Arrow(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val layer: Int = 2,
    val arrowType: String = "straight"
) : SceneObject()

@Serializable
data class FreeformPath(
    override val id: String,
    override val x: Float = 0f,
    override val y: Float = 0f,
    override val rotation: Float = 0f,
    override val scale: Float = 1f,
    override val layer: Int = 2,
    val points: List<ConnectorPoint> = emptyList(),
    val color: String = "#FF0000",
    val strokeWidth: Float = 4f
) : SceneObject()

@Serializable
data class ConnectorPoint(
    val x: Float,
    val y: Float
)
