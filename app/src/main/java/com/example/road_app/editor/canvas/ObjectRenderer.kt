package com.example.road_app.editor.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.example.road_app.data.model.Arrow
import com.example.road_app.data.model.Car
import com.example.road_app.data.model.FreeformPath
import com.example.road_app.data.model.RoadSegment
import com.example.road_app.data.model.RoadSegmentKind
import com.example.road_app.data.model.SceneObject
import com.example.road_app.data.model.Sign

fun DrawScope.drawSceneObject(obj: SceneObject, isSelected: Boolean = false) {
    translate(left = obj.x, top = obj.y) {
        rotate(degrees = obj.rotation, pivot = Offset.Zero) {
            scale(scaleX = obj.scale, scaleY = obj.scale, pivot = Offset.Zero) {
                when (obj) {
                    is RoadSegment -> drawRoadSegment(obj)
                    is Car -> drawCar(obj)
                    is Sign -> drawSign(obj)
                    is Arrow -> drawArrow(obj)
                    is FreeformPath -> drawFreeformPath(obj)
                }

                if (isSelected) {
                    drawSelectionHighlight()
                }
            }
        }
    }
}

private fun DrawScope.drawSelectionHighlight() {
    drawRect(
        color = Color.Yellow,
        topLeft = Offset(-60f, -60f),
        size = Size(120f, 120f),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawRoadSegment(road: RoadSegment) {
    val color = Color.DarkGray

    when (road.segmentKind) {
        RoadSegmentKind.STRAIGHT -> {
            drawRect(
                color = color,
                topLeft = Offset(-100f, -50f),
                size = Size(200f, 100f)
            )
            drawLine(
                color = Color.Yellow,
                start = Offset(-100f, 0f),
                end = Offset(100f, 0f),
                strokeWidth = 2f
            )
        }
        RoadSegmentKind.CURVE -> {
            drawArc(
                color = color,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = true,
                topLeft = Offset(-100f, -100f),
                size = Size(200f, 200f)
            )
        }
        RoadSegmentKind.T_JUNCTION -> {
            drawRect(
                color = color,
                topLeft = Offset(-50f, -100f),
                size = Size(100f, 200f)
            )
            drawRect(
                color = color,
                topLeft = Offset(-100f, -100f),
                size = Size(200f, 100f)
            )
        }
        RoadSegmentKind.FOUR_WAY -> {
            drawRect(
                color = color,
                topLeft = Offset(-50f, -100f),
                size = Size(100f, 200f)
            )
            drawRect(
                color = color,
                topLeft = Offset(-100f, -50f),
                size = Size(200f, 100f)
            )
        }
        RoadSegmentKind.ROUNDABOUT -> {
            drawCircle(color = color, radius = 80f)
            drawCircle(color = Color.Green, radius = 30f)
        }
    }

    road.connectorPoints.forEach { cp ->
        drawCircle(
            color = Color.Green,
            radius = 6f,
            center = Offset(cp.x, cp.y)
        )
    }
}

private fun DrawScope.drawCar(car: Car) {
    drawRect(
        color = Color.Red,
        topLeft = Offset(-30f, -20f),
        size = Size(60f, 40f)
    )
    drawRect(
        color = Color.Cyan,
        topLeft = Offset(-20f, -15f),
        size = Size(40f, 10f)
    )
}

private fun DrawScope.drawSign(sign: Sign) {
    when (sign.signType) {
        "stop" -> {
            drawCircle(color = Color.Red, radius = 25f)
            drawCircle(color = Color.White, radius = 20f)
        }
        else -> {
            drawCircle(color = Color.Blue, radius = 25f)
        }
    }
}

private fun DrawScope.drawArrow(arrow: Arrow) {
    drawLine(
        color = Color.Magenta,
        start = Offset(-40f, 0f),
        end = Offset(40f, 0f),
        strokeWidth = 4f
    )
    drawLine(
        color = Color.Magenta,
        start = Offset(40f, 0f),
        end = Offset(30f, -10f),
        strokeWidth = 4f
    )
    drawLine(
        color = Color.Magenta,
        start = Offset(40f, 0f),
        end = Offset(30f, 10f),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawFreeformPath(path: FreeformPath) {
    if (path.points.size < 2) return
    val color = try {
        Color(android.graphics.Color.parseColor(path.color))
    } catch (e: Exception) {
        Color.Red
    }
    for (i in 0 until path.points.size - 1) {
        drawLine(
            color = color,
            start = Offset(path.points[i].x, path.points[i].y),
            end = Offset(path.points[i + 1].x, path.points[i + 1].y),
            strokeWidth = path.strokeWidth
        )
    }
}
