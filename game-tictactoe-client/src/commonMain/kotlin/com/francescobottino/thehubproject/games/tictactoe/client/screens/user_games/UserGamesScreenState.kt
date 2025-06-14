package com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games

import kotlinx.datetime.LocalDateTime

data class UserGamesScreenState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    data class Room(
        val id: String,
        val lastUpdate: LocalDateTime,
    )
}