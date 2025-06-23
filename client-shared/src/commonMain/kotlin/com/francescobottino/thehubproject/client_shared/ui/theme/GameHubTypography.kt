package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object GameHubTypography {
    val displayLarge = TextStyle(
        fontWeight = FontWeight.Companion.Bold,
        fontSize = 42.sp,
    )

    val displayMedium = TextStyle(
        fontWeight = FontWeight.Companion.Bold,
        fontSize = 36.sp,
    )

    val headlineLarge = TextStyle(
        fontWeight = FontWeight.Companion.Bold,
        fontSize = 28.sp,
        color = GameHubColors.onPrimary
    )

    val titleLarge = TextStyle(
        fontWeight = FontWeight.Companion.Bold,
        color = GameHubColors.onPrimary
    )

    val titleMedium = TextStyle(
        fontWeight = FontWeight.Companion.SemiBold,
        color = GameHubColors.onPrimary
    )

    val bodyLarge = TextStyle(
        color = GameHubColors.onSurface
    )

    val bodyMedium = TextStyle(
        color = GameHubColors.onPrimaryVariant
    )

    val labelMedium = TextStyle(
        fontWeight = FontWeight.Companion.SemiBold,
        color = GameHubColors.onPrimary
    )
}