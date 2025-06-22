package com.francescobottino.thehubproject.games.tictactoe.client.ui.images

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TicTacToeCross: ImageVector
    get() {
        if (_TicTacToeCross != null) {
            return _TicTacToeCross!!
        }
        _TicTacToeCross = ImageVector.Builder(
            name = "TicTacToeCross",
            defaultWidth = 80.dp,
            defaultHeight = 80.dp,
            viewportWidth = 80f,
            viewportHeight = 80f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(11f, 2f)
                arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.624f, 16.312f)
                lineToRelative(22.374f, 22.406f)
                lineToRelative(-21.622f, 21.626f)
                arcToRelative(9.502f, 9.502f, 0f, isMoreThanHalf = true, isPositiveArc = false, 13.437f, 13.436f)
                lineToRelative(21.623f, -21.626f)
                lineToRelative(22.375f, 22.376f)
                arcToRelative(9.502f, 9.502f, 0f, isMoreThanHalf = true, isPositiveArc = false, 13.438f, -13.437f)
                lineToRelative(-22.376f, -22.376f)
                lineToRelative(21.626f, -21.625f)
                arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.907f, -16.314f)
                arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.53f, 2.875f)
                lineToRelative(-21.626f, 21.628f)
                lineToRelative(-22.375f, -22.407f)
                arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.813f, -2.875f)
                close()
            }
        }.build()

        return _TicTacToeCross!!
    }

@Suppress("ObjectPropertyName")
private var _TicTacToeCross: ImageVector? = null
