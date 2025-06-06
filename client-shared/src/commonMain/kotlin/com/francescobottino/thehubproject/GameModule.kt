package com.francescobottino.thehubproject

import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.DrawableResource

interface GameModule {
    val name: String
    val icon: DrawableResource
    fun getMainScreen(): Screen
}