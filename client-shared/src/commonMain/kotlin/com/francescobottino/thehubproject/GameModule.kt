package com.francescobottino.thehubproject

import cafe.adriel.voyager.core.screen.Screen

interface GameModule {
    fun getName(): String
    fun getMainScreen(): Screen
}