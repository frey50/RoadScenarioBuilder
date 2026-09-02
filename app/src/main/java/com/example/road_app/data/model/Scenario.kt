package com.example.road_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Scenario(
    val id: String,
    val name: String,
    val objects: List<SceneObject> = emptyList(),
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
