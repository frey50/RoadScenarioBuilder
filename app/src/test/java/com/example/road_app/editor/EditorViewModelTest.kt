package com.example.road_app.editor

import com.example.road_app.data.model.CameraState
import com.example.road_app.data.model.Car
import com.example.road_app.data.model.ConnectorPoint
import com.example.road_app.data.model.RoadSegment
import com.example.road_app.data.model.RoadSegmentKind
import com.example.road_app.data.model.Sign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditorViewModelTest {

    private lateinit var viewModel: EditorViewModel

    @Before
    fun setup() {
        viewModel = EditorViewModel(seedData = false)
    }

    @Test
    fun `initial state is empty`() {
        assertTrue(viewModel.sceneObjects.value.isEmpty())
        assertEquals(CameraState(), viewModel.cameraState.value)
        assertNull(viewModel.selectedObjectId.value)
        assertFalse(viewModel.canUndo())
        assertFalse(viewModel.canRedo())
    }

    @Test
    fun `addObject adds to scene and selects it`() {
        val car = Car(id = "car1", x = 100f, y = 200f)
        viewModel.addObject(car)

        assertEquals(1, viewModel.sceneObjects.value.size)
        assertEquals(car, viewModel.sceneObjects.value[0])
        assertEquals("car1", viewModel.selectedObjectId.value)
        assertTrue(viewModel.canUndo())
    }

    @Test
    fun `moveObject updates position`() {
        val car = Car(id = "car1", x = 0f, y = 0f)
        viewModel.addObject(car)
        viewModel.moveObject("car1", 50f, 100f)

        val moved = viewModel.sceneObjects.value[0] as Car
        assertEquals(50f, moved.x, 0.001f)
        assertEquals(100f, moved.y, 0.001f)
    }

    @Test
    fun `rotateObject updates rotation`() {
        val sign = Sign(id = "sign1", x = 0f, y = 0f, rotation = 0f)
        viewModel.addObject(sign)
        viewModel.rotateObject("sign1", 45f)

        val rotated = viewModel.sceneObjects.value[0] as Sign
        assertEquals(45f, rotated.rotation, 0.001f)
    }

    @Test
    fun `scaleObject updates scale`() {
        val car = Car(id = "car1", x = 0f, y = 0f, scale = 1f)
        viewModel.addObject(car)
        viewModel.scaleObject("car1", 2f)

        val scaled = viewModel.sceneObjects.value[0] as Car
        assertEquals(2f, scaled.scale, 0.001f)
    }

    @Test
    fun `removeObject deletes and clears selection`() {
        val car = Car(id = "car1", x = 0f, y = 0f)
        viewModel.addObject(car)
        viewModel.removeObject("car1")

        assertTrue(viewModel.sceneObjects.value.isEmpty())
        assertNull(viewModel.selectedObjectId.value)
    }

    @Test
    fun `selectObject updates selection`() {
        val car = Car(id = "car1", x = 0f, y = 0f)
        val sign = Sign(id = "sign1", x = 10f, y = 10f)
        viewModel.addObject(car)
        viewModel.addObject(sign)

        viewModel.selectObject("sign1")
        assertEquals("sign1", viewModel.selectedObjectId.value)
    }

    @Test
    fun `undo restores previous state`() {
        val car = Car(id = "car1", x = 0f, y = 0f)
        viewModel.addObject(car)
        assertEquals(1, viewModel.sceneObjects.value.size)

        viewModel.undo()
        assertTrue(viewModel.sceneObjects.value.isEmpty())
        assertTrue(viewModel.canRedo())
    }

    @Test
    fun `redo restores undone state`() {
        val car = Car(id = "car1", x = 0f, y = 0f)
        viewModel.addObject(car)
        viewModel.undo()
        assertTrue(viewModel.sceneObjects.value.isEmpty())

        viewModel.redo()
        assertEquals(1, viewModel.sceneObjects.value.size)
        assertEquals("car1", viewModel.sceneObjects.value[0].id)
    }

    @Test
    fun `clearScene removes all objects`() {
        viewModel.addObject(Car(id = "c1", x = 0f, y = 0f))
        viewModel.addObject(Sign(id = "s1", x = 10f, y = 10f))
        viewModel.clearScene()

        assertTrue(viewModel.sceneObjects.value.isEmpty())
        assertNull(viewModel.selectedObjectId.value)
    }

    @Test
    fun `panCamera updates offset`() {
        viewModel.panCamera(100f, 200f)
        assertEquals(100f, viewModel.cameraState.value.offsetX, 0.001f)
        assertEquals(200f, viewModel.cameraState.value.offsetY, 0.001f)
    }

    @Test
    fun `zoomCamera updates zoom`() {
        viewModel.zoomCamera(0f, 0f, 2f)
        assertEquals(2f, viewModel.cameraState.value.zoom, 0.001f)
    }

    @Test
    fun `zoomCamera clamps to min and max`() {
        viewModel.zoomCamera(0f, 0f, 0.01f)
        assertEquals(0.1f, viewModel.cameraState.value.zoom, 0.001f)

        viewModel.resetCamera()
        viewModel.zoomCamera(0f, 0f, 100f)
        assertEquals(5f, viewModel.cameraState.value.zoom, 0.001f)
    }

    @Test
    fun `undo stack is limited to 50`() {
        repeat(55) { index ->
            viewModel.addObject(Car(id = "car$index", x = index.toFloat(), y = 0f))
        }
        repeat(50) {
            assertTrue(viewModel.canUndo())
            viewModel.undo()
        }
        assertEquals(5, viewModel.sceneObjects.value.size)
    }

    @Test
    fun `updateObject replaces entire object`() {
        val road = RoadSegment(
            id = "road1",
            x = 0f,
            y = 0f,
            segmentKind = RoadSegmentKind.STRAIGHT,
            connectorPoints = emptyList()
        )
        viewModel.addObject(road)

        val updated = road.copy(
            x = 50f,
            y = 50f,
            rotation = 90f,
            connectorPoints = listOf(
                ConnectorPoint(0f, -50f),
                ConnectorPoint(0f, 50f)
            )
        )
        viewModel.updateObject(updated)

        val result = viewModel.sceneObjects.value[0] as RoadSegment
        assertEquals(50f, result.x, 0.001f)
        assertEquals(90f, result.rotation, 0.001f)
        assertEquals(2, result.connectorPoints.size)
    }
}
