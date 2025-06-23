package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.ui.graphics.Brush

object GameHubBrushes {
    val backgroundBrush = Brush.Companion.verticalGradient(GameHubColors.backgroundGradient)
    val titleBrush = Brush.Companion.linearGradient(GameHubColors.titleGradient)
    val primaryActionBrush = Brush.Companion.horizontalGradient(GameHubColors.primaryActionGradient)
    val secondaryActionBrush = Brush.Companion.horizontalGradient(GameHubColors.secondaryActionGradient)
    val tertiaryActionBrush = Brush.Companion.horizontalGradient(GameHubColors.tertiaryActionGradient)
}