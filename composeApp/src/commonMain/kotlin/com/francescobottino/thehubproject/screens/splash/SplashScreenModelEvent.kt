package com.francescobottino.thehubproject.screens.splash

import cafe.adriel.voyager.core.screen.Screen

sealed interface SplashScreenModelEvent {
    data class Navigate(val screen: Screen): SplashScreenModelEvent
}