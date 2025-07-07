package com.francescobottino.thehubproject.client_features.core.ui.images

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MainIcon: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "MainIcon",
        defaultWidth = 82.dp,
        defaultHeight = 82.dp,
        viewportWidth = 82f,
        viewportHeight = 82f
    ).apply {
        path(fill = SolidColor(Color(0xFFF6CD6A))) {
            moveTo(6.5f, 61f)
            lineTo(40.9f, 81f)
            lineTo(75.3f, 61f)
            lineTo(75.3f, 21f)
            lineTo(40.9f, 1f)
            lineTo(6.5f, 21f)
            close()
        }
        path(fill = SolidColor(Color(0xFFF6CD6A))) {
            moveTo(58.1f, 71f)
            lineTo(6.5f, 41f)
            lineTo(6.5f, 61f)
            lineTo(40.9f, 81f)
            close()
        }
        path(fill = SolidColor(Color(0xFFF5BF64))) {
            moveTo(40.9f, 61f)
            lineTo(6.5f, 41f)
            lineTo(6.5f, 21f)
            lineTo(23.7f, 11f)
            close()
        }
        path(fill = SolidColor(Color(0xFFEBAB59))) {
            moveTo(40.9f, 1f)
            lineTo(40.9f, 61f)
            lineTo(23.7f, 11f)
            close()
        }
        path(fill = SolidColor(Color(0xFFE8A251))) {
            moveTo(40.9f, 61f)
            lineTo(40.9f, 1f)
            lineTo(58.1f, 11f)
            close()
        }
        path(fill = SolidColor(Color(0xFFE5974B))) {
            moveTo(40.9f, 61f)
            lineTo(58.1f, 11f)
            lineTo(75.3f, 21f)
            close()
        }
        path(fill = SolidColor(Color(0xFFE48341))) {
            moveTo(40.9f, 61f)
            lineTo(75.3f, 21f)
            lineTo(75.3f, 41f)
            close()
        }
        path(fill = SolidColor(Color(0xFFDE7A3D))) {
            moveTo(40.9f, 61f)
            lineTo(75.3f, 41f)
            lineTo(75.3f, 61f)
            close()
        }
        path(fill = SolidColor(Color(0xFFD26E3A))) {
            moveTo(40.9f, 61f)
            lineTo(75.3f, 61f)
            lineTo(58.1f, 71f)
            close()
        }
        path(
            stroke = SolidColor(Color(0xFF333333)),
            strokeLineWidth = 1f
        ) {
            moveTo(6.5f, 61f)
            lineTo(40.9f, 81f)
            lineTo(75.3f, 61f)
            lineTo(75.3f, 21f)
            lineTo(40.9f, 1f)
            lineTo(6.5f, 21f)
            close()
        }
    }.build()
}