package com.francescobottino.thehubproject.client_features.core

import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

interface GameModule {
    val name: String
    val icon: ImageVector
    fun getMainScreen(): Screen
}