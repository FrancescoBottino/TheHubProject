package com.francescobottino.thehubproject.games.tictactoe.model

import kotlinx.serialization.Serializable

@Serializable
enum class TicTacToeRestartGameResponseError {
    ROOM_NOT_FOUND,
    NOT_THE_HOST,
    GAME_NOT_FINISHED;
}