package com.francescobottino.thehubproject.games.tictactoe.shared.model.api

import kotlinx.serialization.Serializable

@Serializable
enum class TicTacToeCloseGameResponseError {
    ROOM_NOT_FOUND,
    ROOM_ALREADY_CLOSED,
    PLAYER_NOT_IN_ROOM;
}