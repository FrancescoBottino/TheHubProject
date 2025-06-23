package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

object GameHubThemeUtils {
    @Composable
    fun getBackgroundBrush() = Brush.Companion.verticalGradient(GameHubTheme.colors.background)

    @Composable
    fun getTitleBrush() = Brush.Companion.linearGradient(GameHubTheme.colors.title)

    @Composable
    fun getPrimaryActionBrush() = Brush.Companion.horizontalGradient(GameHubTheme.colors.primaryAction)

    @Composable
    fun getSecondaryActionBrush() = Brush.Companion.horizontalGradient(GameHubTheme.colors.secondaryAction)

    @Composable
    fun getTertiaryActionBrush() = Brush.Companion.horizontalGradient(GameHubTheme.colors.tertiaryAction)
}