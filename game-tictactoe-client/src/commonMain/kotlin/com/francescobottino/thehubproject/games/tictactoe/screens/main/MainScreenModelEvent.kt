package com.francescobottino.thehubproject.games.tictactoe.screens.main

import cafe.adriel.voyager.core.screen.Screen

sealed interface MainScreenModelEvent {
    data class Navigate(val screen: Screen): MainScreenModelEvent
}