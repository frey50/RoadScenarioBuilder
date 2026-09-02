package com.example.road_app.editor

import androidx.lifecycle.ViewModel
import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.Car
import com.example.road_app.data.model.FreeformPath
import com.example.road_app.data.model.RoadSegment
import com.example.road_app.data.model.SceneObject
import com.example.road_app.data.model.Sign
import com.example.road_app.data.model.Arrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditorViewModel : ViewModel() {

    private val _sceneObjects = MutableStateFlow<List<SceneObject>>(emptyList())
    val sceneObjects: StateFlow<List<SceneObject>> = _sceneObjects.asStateFlow()

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _selectedObjectId = MutableStateFlow<String?>(null)
    val selectedObjectId: StateFlow<String?> = _selectedObjectId.asStateFlow()

    // Undo/Redo stacks (snapshot-based)
    private val undoStack = ArrayDeque<List<SceneObject>>()
    private val redoStack = ArrayDeque<List<SceneObject>>()
    private val maxUndoSize = 50

    // ─── Scene Object Operations ───

    fun addObject(obj: SceneObject) {
        saveStateForUndo()
        _sceneObjects.value = _sceneObjects.value + obj
        _selectedObjectId.value = obj.id
    }

    fun removeObject(id: String) {
        saveStateForUndo()
        _sceneObjects.value = _sceneObjects.value.filter { it.id != id }
        if (_selectedObjectId.value == id) {
            _selectedObjectId.value = null
        }
    }

    fun moveObject(id: String, x: Float, y: Float) {
        saveStateForUndo()
        _sceneObjects.value = _sceneObjects.value.map {
            if (it.id == id) it.withPosition(x, y) else it
        }
    }

    fun rotateObject(id: String, rotation: Float) {
        saveStateForUndo()
        _sceneObjects.value = _sceneObjects.value.map {
            if (it.id == id) it.withRotation(rotation) else it
        }
    }

    fun scaleObject(id: String, scale: Float) {
        saveStateForUndo()
        _sceneObjects.value = _sceneObjects.value.map {
            if (it.id == id) it.withScale(scale) else it
        }
    }

    fun updateObject(updated: SceneObject) {
        saveStateForUndo()
        _sceneObjects.value = _sceneObjects.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun selectObject(id: String?) {
        _selectedObjectId.value = id
    }

    fun clearScene() {
        saveStateForUndo()
        _sceneObjects.value = emptyList()
        _selectedObjectId.value = null
    }

    // ─── Camera Operations ───

    fun panCamera(dx: Float, dy: Float) {
        _cameraState.value = _cameraState.value.copy(
            offsetX = _cameraState.value.offsetX + dx,
            offsetY = _cameraState.value.offsetY + dy
        )
    }

    fun zoomCamera(centerX: Float, centerY: Float, factor: Float) {
        val current = _cameraState.value
        val newZoom = (current.zoom * factor).coerceIn(0.1f, 5f)
        val zoomRatio = newZoom / current.zoom
        val newOffsetX = centerX - (centerX - current.offsetX) * zoomRatio
        val newOffsetY = centerY - (centerY - current.offsetY) * zoomRatio
        _cameraState.value = current.copy(
            offsetX = newOffsetX,
            offsetY = newOffsetY,
            zoom = newZoom
        )
    }

    fun resetCamera() {
        _cameraState.value = CameraState()
    }

    // ─── Undo / Redo ───

    fun undo() {
        if (undoStack.isEmpty()) return
        redoStack.addLast(_sceneObjects.value)
        _sceneObjects.value = undoStack.removeLast()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        undoStack.addLast(_sceneObjects.value)
        _sceneObjects.value = redoStack.removeLast()
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    // ─── Helpers ───

    private fun saveStateForUndo() {
        undoStack.addLast(_sceneObjects.value)
        if (undoStack.size > maxUndoSize) undoStack.removeFirst()
        redoStack.clear()
    }

    private fun SceneObject.withPosition(x: Float, y: Float): SceneObject = when (this) {
        is RoadSegment -> copy(x = x, y = y)
        is Car -> copy(x = x, y = y)
        is Sign -> copy(x = x, y = y)
        is Arrow -> copy(x = x, y = y)
        is FreeformPath -> copy(x = x, y = y)
    }

    private fun SceneObject.withRotation(rotation: Float): SceneObject = when (this) {
        is RoadSegment -> copy(rotation = rotation)
        is Car -> copy(rotation = rotation)
        is Sign -> copy(rotation = rotation)
        is Arrow -> copy(rotation = rotation)
        is FreeformPath -> copy(rotation = rotation)
    }

    private fun SceneObject.withScale(scale: Float): SceneObject = when (this) {
        is RoadSegment -> copy(scale = scale)
        is Car -> copy(scale = scale)
        is Sign -> copy(scale = scale)
        is Arrow -> copy(scale = scale)
        is FreeformPath -> copy(scale = scale)
    }
}
