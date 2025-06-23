package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.ui.graphics.Color

data class GameHubThemeColors(
    val background: List<Color> = GameHubColors.backgroundGradient,
    val title: List<Color> = GameHubColors.titleGradient,
    val primaryAction: List<Color> = GameHubColors.primaryActionGradient,
    val secondaryAction: List<Color> = GameHubColors.secondaryActionGradient,
    val tertiaryAction: List<Color> = GameHubColors.tertiaryActionGradient,
    val accent: Color = GameHubColors.accent,
    val accentSoft: Color = GameHubColors.accentSoft,
    val surfaceOverlay: Color = GameHubColors.surfaceOverlay,
    val surfaceHighlight: Color = GameHubColors.surfaceHighlight,
    val onPrimary: Color = GameHubColors.onPrimary,
    val onPrimaryVariant: Color = GameHubColors.onPrimaryVariant,
    val onSurface: Color = GameHubColors.onSurface,
    val onSurfaceVariant: Color = GameHubColors.onSurfaceVariant,
    val border: Color = GameHubColors.border,
    val borderFocused: Color = GameHubColors.borderFocused,
)