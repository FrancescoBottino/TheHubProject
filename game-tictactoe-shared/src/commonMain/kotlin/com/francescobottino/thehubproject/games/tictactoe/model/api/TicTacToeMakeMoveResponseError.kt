package com.francescobottino.thehubproject.games.tictactoe.model.api

import kotlinx.serialization.Serializable

@Serializable
enum class TicTacToeMakeMoveResponseError {
    ROOM_NOT_FOUND,
    PLAYER_NOT_IN_ROOM,
    NOT_YOUR_TURN,
    GAME_NOT_IN_PROGRESS;
}