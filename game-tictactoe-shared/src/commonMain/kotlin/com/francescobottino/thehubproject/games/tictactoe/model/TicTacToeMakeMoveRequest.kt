package com.francescobottino.thehubproject.games.tictactoe.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeMakeMoveRequest(
    val cell: TicTacToeBoardCell,
)