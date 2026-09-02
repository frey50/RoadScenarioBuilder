package com.example.road_app.editor.canvas

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.SceneObject
import com.example.road_app.util.hitTest
import com.example.road_app.util.toWorld

fun Modifier.canvasGestures(
    cameraState: CameraState,
    roadObjects: List<SceneObject>,
    selectedId: String?,
    onSelectObject: (String?) -> Unit,
    onMoveObject: (String, Float, Float) -> Unit,
    onPanCamera: (Float, Float) -> Unit,
    onZoomCamera: (Float, Float, Float) -> Unit
): Modifier = pointerInput(selectedId) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown()
            down.consume()

            val worldPoint = down.position.toWorld(cameraState)
            val hitRoad = roadObjects
                .sortedByDescending { it.layer }
                .firstOrNull { hitTest(worldPoint, it) }

            if (hitRoad != null) {
                onSelectObject(hitRoad.id)
                var previous = down.position
                var isTwoFinger = false
                var secondId: PointerId? = null
                var initialDistance = 0f
                var initialZoom = cameraState.zoom

                do {
                    val event = awaitPointerEvent()
                    val pressed = event.changes.filter { it.pressed }

                    // Switch to zoom if second finger detected
                    if (!isTwoFinger && pressed.size >= 2) {
                        isTwoFinger = true
                        secondId = pressed[1].id
                        val p1 = pressed[0].position
                        val p2 = pressed[1].position
                        initialDistance = (p1 - p2).getDistance()
                        initialZoom = cameraState.zoom
                    }

                    if (isTwoFinger && pressed.size >= 2) {
                        val c1 = pressed.firstOrNull { it.id == down.id } ?: continue
                        val c2 = pressed.firstOrNull { it.id == secondId } ?: continue
                        val p1 = c1.position
                        val p2 = c2.position
                        val distance = (p1 - p2).getDistance()

                        if (initialDistance > 0f) {
                            val targetZoom = (initialZoom * (distance / initialDistance)).coerceIn(0.1f, 5f)
                            val factor = targetZoom / cameraState.zoom
                            val centerX = (p1.x + p2.x) / 2f
                            val centerY = (p1.y + p2.y) / 2f
                            onZoomCamera(centerX, centerY, factor)
                        }
                        c1.consume()
                        c2.consume()
                    } else if (!isTwoFinger) {
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change != null && change.positionChanged()) {
                            val worldDelta = Offset(
                                (change.position.x - previous.x) / cameraState.zoom,
                                (change.position.y - previous.y) / cameraState.zoom
                            )
                            onMoveObject(hitRoad.id, hitRoad.x + worldDelta.x, hitRoad.y + worldDelta.y)
                            previous = change.position
                            change.consume()
                        }
                    }
                } while (event.changes.any { it.pressed })
            } else {
                onSelectObject(null)
                var previous = down.position
                var isTwoFinger = false
                var secondId: PointerId? = null
                var initialDistance = 0f
                var initialZoom = cameraState.zoom

                do {
                    val event = awaitPointerEvent()
                    val pressed = event.changes.filter { it.pressed }

                    if (!isTwoFinger && pressed.size >= 2) {
                        isTwoFinger = true
                        secondId = pressed[1].id
                        val p1 = pressed[0].position
                        val p2 = pressed[1].position
                        initialDistance = (p1 - p2).getDistance()
                        initialZoom = cameraState.zoom
                    }

                    if (isTwoFinger && pressed.size >= 2) {
                        val c1 = pressed.firstOrNull { it.id == down.id } ?: continue
                        val c2 = pressed.firstOrNull { it.id == secondId } ?: continue
                        val p1 = c1.position
                        val p2 = c2.position
                        val distance = (p1 - p2).getDistance()

                        if (initialDistance > 0f) {
                            val targetZoom = (initialZoom * (distance / initialDistance)).coerceIn(0.1f, 5f)
                            val factor = targetZoom / cameraState.zoom
                            val centerX = (p1.x + p2.x) / 2f
                            val centerY = (p1.y + p2.y) / 2f
                            onZoomCamera(centerX, centerY, factor)
                        }
                        c1.consume()
                        c2.consume()
                    } else if (!isTwoFinger) {
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change != null && change.positionChanged()) {
                            onPanCamera(change.position.x - previous.x, change.position.y - previous.y)
                            previous = change.position
                            change.consume()
                        }
                    }
                } while (event.changes.any { it.pressed })
            }
        }
    }
}
