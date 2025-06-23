package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object GameHubTheme {
    val colors: GameHubThemeColors
        @Composable
        get() = LocalGameHubColors.current

    @Composable
    operator fun invoke(
        darkTheme: Boolean = isSystemInDarkTheme(),
        colors: GameHubThemeColors = GameHubThemeColors(),
        content: @Composable () -> Unit
    ) {
        val materialColors = darkColorScheme(
            primary = GameHubColors.accent,
            onPrimary = GameHubColors.onPrimary,
            surface = GameHubColors.surfaceOverlay,
            onSurface = GameHubColors.onSurface,
            background = GameHubColors.backgroundGradient.first(),
            onBackground = GameHubColors.onPrimary,
        )

        val typography = Typography(
            displayLarge = GameHubTypography.displayLarge,
            displayMedium = GameHubTypography.displayMedium,
            headlineLarge = GameHubTypography.headlineLarge,
            titleLarge = GameHubTypography.titleLarge,
            titleMedium = GameHubTypography.titleMedium,
            bodyLarge = GameHubTypography.bodyLarge,
            bodyMedium = GameHubTypography.bodyMedium,
            labelMedium = GameHubTypography.labelMedium,
        )

        CompositionLocalProvider(LocalGameHubColors provides colors) {
            MaterialTheme(
                colorScheme = materialColors,
                typography = typography,
                content = content
            )
        }
    }
}

