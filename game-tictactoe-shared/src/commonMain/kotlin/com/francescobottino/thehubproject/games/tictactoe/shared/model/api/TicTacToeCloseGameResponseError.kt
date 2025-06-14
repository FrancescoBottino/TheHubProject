package com.francescobottino.thehubproject.games.tictactoe.shared.model.api

import kotlinx.serialization.Serializable

@Serializable
enum class TicTacToeCloseGameResponseError {
    ROOM_NOT_FOUND,
    PLAYER_NOT_IN_ROOM;
    //TODO
}