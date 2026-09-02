package com.example.road_app.editor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.road_app.data.model.Arrow
import com.example.road_app.data.model.Car
import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.FreeformPath
import com.example.road_app.data.model.RoadSegment
import com.example.road_app.data.model.SceneObject
import com.example.road_app.data.model.Sign
// import com.example.road_app.editor.manipulables.ManipulableObject
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

@Composable
fun SceneCanvas(
    sceneObjects: List<SceneObject>,
    cameraState: CameraState,
    selectedObjectId: String?,
    onSelectObject: (String?) -> Unit,
    onMoveObject: (String, Float, Float) -> Unit,
    onRotateObject: (String, Float) -> Unit,
    onScaleObject: (String, Float) -> Unit,
    onPanCamera: (Float, Float) -> Unit,
    onZoomCamera: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val roadObjects = remember(sceneObjects) {
        sceneObjects.filter { it is RoadSegment || it is FreeformPath }
    }
    val manipulableObjects = remember(sceneObjects) {
        sceneObjects.filter { it is Car || it is Sign || it is Arrow }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Canvas for roads, grid, freeform paths
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
                .canvasGestures(
                    cameraState = cameraState,
                    roadObjects = sceneObjects, // Pass all objects for hit testing
                    selectedId = selectedObjectId,
                    onSelectObject = onSelectObject,
                    onMoveObject = onMoveObject,
                    onPanCamera = onPanCamera,
                    onZoomCamera = onZoomCamera
                )
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

        // Overlay composables for cars, signs, arrows
        // manipulableObjects.forEach { obj ->
        //    ManipulableObject(
        //        obj = obj,
        //        isSelected = obj.id == selectedObjectId,
        //        cameraState = cameraState,
        //        onSelect = { onSelectObject(obj.id) },
        //        onMove = { x, y -> onMoveObject(obj.id, x, y) },
        //        onRotate = { rotation -> onRotateObject(obj.id, rotation) },
        //        onScale = { scale -> onScaleObject(obj.id, scale) }
        //    )
        // }
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
