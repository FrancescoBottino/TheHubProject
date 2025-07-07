package com.francescobottino.thehubproject.games.tictactoe.client.screens.join_room

sealed interface JoinRoomScreenEvent {
    data object Retry: JoinRoomScreenEvent
}