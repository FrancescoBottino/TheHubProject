package com.francescobottino.thehubproject.games.tictactoe.screens.game

sealed interface GameScreenModelEvent {
    data class OnConnectionLost(val message: String? = null): GameScreenModelEvent
}