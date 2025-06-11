package com.francescobottino.thehubproject.games.tictactoe.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TicTacToeCatGame: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TicTacToeCatGame",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(11.75f, 6.406f)
            curveTo(10.27f, 6.406f, 10.122f, 6.563f, 9.356f, 6.563f)
            curveTo(8.718f, 6.563f, 6.802f, 5f, 5.845f, 5f)
            curveTo(4.887f, 5f, 3.77f, 5.563f, 3.77f, 7.188f)
            verticalLineTo(9.063f)
            curveTo(3.772f, 9.555f, 3.951f, 11.063f, 4.651f, 10.66f)
            curveTo(3.823f, 11.638f, 3.74f, 12.779f, 3.751f, 13.883f)
            curveTo(3.528f, 13.947f, 3.301f, 14.02f, 3.08f, 14.095f)
            curveTo(2.396f, 14.329f, 1.671f, 14.627f, 1.343f, 14.839f)
            curveTo(0.995f, 15.063f, 0.895f, 15.528f, 1.12f, 15.876f)
            curveTo(1.345f, 16.224f, 1.809f, 16.323f, 2.157f, 16.099f)
            curveTo(2.313f, 15.998f, 2.878f, 15.749f, 3.565f, 15.514f)
            curveTo(3.641f, 15.488f, 3.717f, 15.463f, 3.793f, 15.439f)
            curveTo(3.839f, 15.872f, 3.954f, 16.268f, 4.125f, 16.629f)
            lineTo(4.101f, 16.642f)
            curveTo(3.691f, 16.858f, 3.311f, 17.107f, 3.069f, 17.265f)
            curveTo(3.027f, 17.293f, 2.989f, 17.317f, 2.956f, 17.339f)
            curveTo(2.608f, 17.563f, 2.508f, 18.028f, 2.733f, 18.376f)
            curveTo(2.958f, 18.724f, 3.422f, 18.823f, 3.77f, 18.599f)
            curveTo(3.811f, 18.572f, 3.855f, 18.544f, 3.901f, 18.513f)
            curveTo(4.146f, 18.353f, 4.46f, 18.149f, 4.802f, 17.968f)
            curveTo(4.882f, 17.925f, 4.959f, 17.887f, 5.033f, 17.852f)
            curveTo(6.763f, 19.475f, 9.87f, 20f, 11.75f, 20f)
            curveTo(13.63f, 20f, 16.737f, 19.475f, 18.467f, 17.852f)
            curveTo(18.541f, 17.887f, 18.618f, 17.925f, 18.698f, 17.968f)
            curveTo(19.04f, 18.149f, 19.354f, 18.353f, 19.599f, 18.513f)
            curveTo(19.645f, 18.544f, 19.689f, 18.572f, 19.73f, 18.599f)
            curveTo(20.078f, 18.823f, 20.542f, 18.724f, 20.767f, 18.376f)
            curveTo(20.992f, 18.028f, 20.892f, 17.563f, 20.544f, 17.339f)
            curveTo(20.511f, 17.317f, 20.473f, 17.293f, 20.431f, 17.265f)
            curveTo(20.189f, 17.107f, 19.809f, 16.858f, 19.399f, 16.642f)
            lineTo(19.375f, 16.629f)
            curveTo(19.546f, 16.268f, 19.661f, 15.872f, 19.707f, 15.439f)
            curveTo(19.783f, 15.463f, 19.859f, 15.488f, 19.935f, 15.514f)
            curveTo(20.622f, 15.749f, 21.187f, 15.998f, 21.344f, 16.099f)
            curveTo(21.691f, 16.323f, 22.156f, 16.224f, 22.381f, 15.876f)
            curveTo(22.605f, 15.528f, 22.505f, 15.063f, 22.157f, 14.839f)
            curveTo(21.83f, 14.627f, 21.104f, 14.329f, 20.42f, 14.095f)
            curveTo(20.2f, 14.019f, 19.972f, 13.947f, 19.749f, 13.882f)
            curveTo(19.76f, 12.778f, 19.677f, 11.638f, 18.849f, 10.66f)
            curveTo(19.549f, 11.063f, 19.728f, 9.555f, 19.73f, 9.063f)
            verticalLineTo(7.188f)
            curveTo(19.73f, 5.563f, 18.613f, 5f, 17.655f, 5f)
            curveTo(16.698f, 5f, 14.783f, 6.563f, 14.144f, 6.563f)
            curveTo(13.378f, 6.563f, 13.231f, 6.406f, 11.75f, 6.406f)
            close()
            moveTo(11.075f, 15.6f)
            curveTo(11.277f, 15.531f, 11.516f, 15.5f, 11.75f, 15.5f)
            curveTo(11.984f, 15.5f, 12.223f, 15.531f, 12.426f, 15.6f)
            curveTo(12.525f, 15.634f, 12.647f, 15.688f, 12.754f, 15.774f)
            curveTo(12.861f, 15.86f, 13f, 16.021f, 13f, 16.25f)
            curveTo(13f, 16.479f, 12.861f, 16.64f, 12.754f, 16.726f)
            curveTo(12.647f, 16.812f, 12.525f, 16.866f, 12.426f, 16.9f)
            curveTo(12.223f, 16.969f, 11.984f, 17f, 11.75f, 17f)
            curveTo(11.516f, 17f, 11.277f, 16.969f, 11.075f, 16.9f)
            curveTo(10.975f, 16.866f, 10.854f, 16.812f, 10.746f, 16.726f)
            curveTo(10.639f, 16.64f, 10.5f, 16.479f, 10.5f, 16.25f)
            curveTo(10.5f, 16.021f, 10.639f, 15.86f, 10.746f, 15.774f)
            curveTo(10.854f, 15.688f, 10.975f, 15.634f, 11.075f, 15.6f)
            close()
            moveTo(13.92f, 12.5f)
            curveTo(14.057f, 12.272f, 14.326f, 12f, 14.73f, 12f)
            curveTo(15.134f, 12f, 15.404f, 12.272f, 15.54f, 12.5f)
            curveTo(15.682f, 12.739f, 15.75f, 13.027f, 15.75f, 13.313f)
            curveTo(15.75f, 13.598f, 15.682f, 13.886f, 15.54f, 14.125f)
            curveTo(15.404f, 14.353f, 15.134f, 14.625f, 14.73f, 14.625f)
            curveTo(14.326f, 14.625f, 14.057f, 14.353f, 13.92f, 14.125f)
            curveTo(13.778f, 13.886f, 13.71f, 13.598f, 13.71f, 13.313f)
            curveTo(13.71f, 13.027f, 13.778f, 12.739f, 13.92f, 12.5f)
            close()
            moveTo(7.96f, 12.5f)
            curveTo(8.097f, 12.272f, 8.366f, 12f, 8.77f, 12f)
            curveTo(9.174f, 12f, 9.444f, 12.272f, 9.58f, 12.5f)
            curveTo(9.722f, 12.739f, 9.79f, 13.027f, 9.79f, 13.313f)
            curveTo(9.79f, 13.598f, 9.722f, 13.886f, 9.58f, 14.125f)
            curveTo(9.444f, 14.353f, 9.174f, 14.625f, 8.77f, 14.625f)
            curveTo(8.366f, 14.625f, 8.097f, 14.353f, 7.96f, 14.125f)
            curveTo(7.818f, 13.886f, 7.75f, 13.598f, 7.75f, 13.313f)
            curveTo(7.75f, 13.027f, 7.818f, 12.739f, 7.96f, 12.5f)
            close()
        }
    }.build()
}