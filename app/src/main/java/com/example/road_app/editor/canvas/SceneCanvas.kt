package com.example.road_app.editor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.SceneObject

@Composable
fun SceneCanvas(
    sceneObjects: List<SceneObject>,
    cameraState: CameraState,
    selectedObjectId: String?,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        translate(left = cameraState.offsetX, top = cameraState.offsetY) {
            scale(scaleX = cameraState.zoom, scaleY = cameraState.zoom, pivot = Offset.Zero) {
                drawGrid()
                sceneObjects.sortedBy { it.layer }.forEach { obj ->
                    drawSceneObject(obj, isSelected = obj.id == selectedObjectId)
                }
            }
        }
    }
}

private fun DrawScope.drawGrid() {
    val gridSize = 100f
    val gridColor = Color(0xFF2A2A4E)
    val majorGridColor = Color(0xFF3A3A5E)

    for (i in -50..50) {
        val pos = i * gridSize
        drawLine(
            color = if (i % 5 == 0) majorGridColor else gridColor,
            start = Offset(pos, -5000f),
            end = Offset(pos, 5000f),
            strokeWidth = if (i % 5 == 0) 2f else 1f
        )
        drawLine(
            color = if (i % 5 == 0) majorGridColor else gridColor,
            start = Offset(-5000f, pos),
            end = Offset(5000f, pos),
            strokeWidth = if (i % 5 == 0) 2f else 1f
        )
    }
}
