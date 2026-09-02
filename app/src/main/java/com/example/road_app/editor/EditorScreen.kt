package com.example.road_app.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.road_app.editor.canvas.SceneCanvas

@Composable
fun EditorScreen(
    onBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val sceneObjects by viewModel.sceneObjects.collectAsState()
    val cameraState by viewModel.cameraState.collectAsState()
    val selectedId by viewModel.selectedObjectId.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        SceneCanvas(
            sceneObjects = sceneObjects,
            cameraState = cameraState,
            selectedObjectId = selectedId,
            onSelectObject = { viewModel.selectObject(it) },
            onMoveObject = { id, x, y -> viewModel.moveObject(id, x, y) },
            onRotateObject = { id, rotation -> viewModel.rotateObject(id, rotation) },
            onScaleObject = { id, scale -> viewModel.scaleObject(id, scale) },
            onPanCamera = { dx, dy -> viewModel.panCamera(dx, dy) },
            onZoomCamera = { cx, cy, factor -> viewModel.zoomCamera(cx, cy, factor) },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Back to List")
        }
    }
}
