package com.francescobottino.thehubproject.games.tictactoe.screens.main

sealed interface MainScreenModelEvent {
    data class NavigateToGameScreen(val roomId: String): MainScreenModelEvent
}