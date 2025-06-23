package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.ui.graphics.Color

object GameHubColors {
    // Background gradients
    val backgroundGradient = listOf(
        Color(0xFF1A1A2E),
        Color(0xFF16213E),
        Color(0xFF0F3460)
    )
    
    val titleGradient = listOf(
        Color.White,
        Color(0xFFE0E0E0)
    )
    
    // Primary action colors
    val primaryActionGradient = listOf(
        Color(0xFF6C63FF),
        Color(0xFF5A52FF)
    )
    
    val secondaryActionGradient = listOf(
        Color(0xFF00D4AA),
        Color(0xFF00B894)
    )
    
    val tertiaryActionGradient = listOf(
        Color(0xFF6C63FF).copy(alpha = 0.8f),
        Color(0xFF5A52FF).copy(alpha = 0.6f),
        Color(0xFF4A42E8).copy(alpha = 0.4f)
    )
    
    // Accent colors
    val accent = Color(0xFFFF6B6B)
    val accentSoft = Color(0xFFFF6B6B).copy(alpha = 0.2f)
    
    // Surface colors
    val surfaceOverlay = Color.White.copy(alpha = 0.1f)
    val surfaceHighlight = Color.White.copy(alpha = 0.2f)
    
    // Text colors
    val onPrimary = Color.White
    val onPrimaryVariant = Color.White.copy(alpha = 0.8f)
    val onSurface = Color.White.copy(alpha = 0.7f)
    val onSurfaceVariant = Color.White.copy(alpha = 0.6f)
    
    // Border colors
    val border = Color.White.copy(alpha = 0.3f)
    val borderFocused = accent
}