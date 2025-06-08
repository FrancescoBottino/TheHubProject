package com.francescobottino.thehubproject.games.tictactoe.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TicTacToeCircle: ImageVector
    get() {
        if (_TicTacToeCircle != null) {
            return _TicTacToeCircle!!
        }
        _TicTacToeCircle = ImageVector.Builder(
            name = "TicTacToeCircle",
            defaultWidth = 100.dp,
            defaultHeight = 96.dp,
            viewportWidth = 100f,
            viewportHeight = 96f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(50f, 0f)
                curveTo(36.93f, 0f, 24.23f, 4.94f, 14.84f, 13.84f)
                curveTo(5.46f, 22.75f, 0f, 35.09f, 0f, 47.9f)
                reflectiveCurveToRelative(5.46f, 25.16f, 14.84f, 34.06f)
                reflectiveCurveTo(36.93f, 95.84f, 50f, 95.84f)
                reflectiveCurveToRelative(25.77f, -4.97f, 35.16f, -13.88f)
                curveTo(94.54f, 73.06f, 100f, 60.72f, 100f, 47.91f)
                curveToRelative(0f, -12.81f, -5.46f, -25.16f, -14.84f, -34.06f)
                curveTo(75.77f, 4.94f, 63.07f, 0f, 50f, 0f)
                close()
                moveTo(50f, 19f)
                curveToRelative(8.14f, 0f, 16.49f, 3.34f, 22.09f, 8.66f)
                curveToRelative(5.61f, 5.32f, 8.94f, 12.95f, 8.94f, 20.25f)
                reflectiveCurveToRelative(-3.33f, 14.96f, -8.94f, 20.28f)
                curveToRelative(-5.61f, 5.32f, -13.96f, 8.63f, -22.09f, 8.63f)
                reflectiveCurveToRelative(-16.49f, -3.3f, -22.09f, -8.62f)
                curveToRelative(-5.61f, -5.32f, -8.94f, -12.98f, -8.94f, -20.28f)
                curveToRelative(0f, -7.3f, 3.33f, -14.93f, 8.94f, -20.25f)
                curveTo(33.52f, 22.34f, 41.86f, 19f, 50f, 19f)
                close()
            }
        }.build()

        return _TicTacToeCircle!!
    }

@Suppress("ObjectPropertyName")
private var _TicTacToeCircle: ImageVector? = null
