package com.francescobottino.thehubproject.games.tictactoe.client.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TicTacToeCrown: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TicTacToeCrown",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 34f,
        viewportHeight = 34f
    ).apply {
        path(
            fill = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color(0xFFFFC923),
                    1f to Color(0xFFFFAD41)
                ),
                start = Offset(17f, 6f),
                end = Offset(17f, 28f)
            ),
            strokeLineWidth = 1f
        ) {
            moveTo(17.917f, 6.219f)
            curveTo(18.339f, 6.434f, 18.672f, 6.79f, 18.853f, 7.227f)
            lineTo(22.648f, 16.405f)
            lineTo(30.444f, 6.906f)
            curveTo(31.138f, 6.06f, 32.398f, 5.928f, 33.257f, 6.612f)
            curveTo(33.727f, 6.985f, 34f, 7.548f, 34f, 8.143f)
            lineTo(34f, 25.047f)
            curveTo(34f, 26.678f, 32.657f, 28f, 31f, 28f)
            lineTo(3f, 28f)
            curveTo(1.343f, 28f, 0f, 26.678f, 0f, 25.047f)
            lineTo(0f, 8.143f)
            curveTo(0f, 7.055f, 0.895f, 6.174f, 2f, 6.174f)
            curveTo(2.604f, 6.174f, 3.176f, 6.443f, 3.556f, 6.905f)
            lineTo(11.354f, 16.405f)
            lineTo(15.148f, 7.227f)
            curveTo(15.544f, 6.268f, 16.621f, 5.783f, 17.607f, 6.093f)
            lineTo(17.754f, 6.146f)
            lineTo(17.917f, 6.219f)
            close()
        }
    }.build()
}
