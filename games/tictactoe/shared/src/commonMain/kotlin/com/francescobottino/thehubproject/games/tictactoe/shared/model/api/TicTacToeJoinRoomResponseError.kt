package com.francescobottino.thehubproject.games.tictactoe.shared.model.api

import kotlinx.serialization.Serializable

@Serializable
enum class TicTacToeJoinRoomResponseError {
    ROOM_NOT_FOUND,
    ROOM_ALREADY_FULL,
    PLAYER_ALREADY_IN_ROOM;
}