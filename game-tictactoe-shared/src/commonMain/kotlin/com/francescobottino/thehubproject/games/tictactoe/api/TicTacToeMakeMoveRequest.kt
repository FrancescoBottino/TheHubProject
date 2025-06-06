package com.francescobottino.thehubproject.games.tictactoe.api

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeMakeMoveRequest(
    val cell: TicTacToeBoardCell,
)