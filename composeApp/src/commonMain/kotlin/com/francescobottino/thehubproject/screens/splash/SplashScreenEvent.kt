package com.francescobottino.thehubproject.screens.splash

sealed interface SplashScreenEvent {
    data object TryAgain: SplashScreenEvent
}