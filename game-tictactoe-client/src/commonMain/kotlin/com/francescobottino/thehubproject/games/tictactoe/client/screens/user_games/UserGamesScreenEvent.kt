package com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games

sealed interface UserGamesScreenEvent {
    data object OnDialogClosed: UserGamesScreenEvent
    data class OnRoomClicked(val room: UserGamesScreenState.Room): UserGamesScreenEvent
}