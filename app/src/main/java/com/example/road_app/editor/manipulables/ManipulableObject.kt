package com.example.road_app.editor.manipulables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.road_app.data.model.Arrow
import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.Car
import com.example.road_app.data.model.FreeformPath
import com.example.road_app.data.model.RoadSegment
import com.example.road_app.data.model.SceneObject
import com.example.road_app.data.model.Sign
import com.example.road_app.editor.canvas.drawSceneObject

@Composable
fun ManipulableObject(
    obj: SceneObject,
    isSelected: Boolean,
    cameraState: CameraState,
    onSelect: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onRotate: (Float) -> Unit,
    onScale: (Float) -> Unit
) {
    // Screen position = (world position * zoom) + camera offset
    val screenX = obj.x * cameraState.zoom + cameraState.offsetX
    val screenY = obj.y * cameraState.zoom + cameraState.offsetY

    Canvas(
        modifier = Modifier
            .size(150.dp) // Bounding box for hit testing
            .graphicsLayer {
                translationX = screenX - size.width / 2f
                translationY = screenY - size.height / 2f
                rotationZ = obj.rotation
                scaleX = obj.scale * cameraState.zoom
                scaleY = obj.scale * cameraState.zoom
            }
            .pointerInput(obj.id) {
                detectTapGestures(onTap = { onSelect() })
            }
            .pointerInput(obj.id) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    onSelect()
                    if (pan != Offset.Zero) {
                        onMove(obj.x + pan.x / cameraState.zoom, obj.y + pan.y / cameraState.zoom)
                    }
                    if (zoom != 1f) {
                        onScale(obj.scale * zoom)
                    }
                    if (rotation != 0f) {
                        onRotate(obj.rotation + rotation)
                    }
                }
            }
    ) {
        // Render the object at (0,0) local position
        val centeredObj = when (obj) {
            is Car -> obj.copy(x = 0f, y = 0f, rotation = 0f, scale = 1f)
            is Sign -> obj.copy(x = 0f, y = 0f, rotation = 0f, scale = 1f)
            is Arrow -> obj.copy(x = 0f, y = 0f, rotation = 0f, scale = 1f)
            is RoadSegment -> obj.copy(x = 0f, y = 0f, rotation = 0f, scale = 1f)
            is FreeformPath -> obj.copy(x = 0f, y = 0f, rotation = 0f, scale = 1f)
        }
        drawSceneObject(centeredObj, isSelected = isSelected)
    }
}
