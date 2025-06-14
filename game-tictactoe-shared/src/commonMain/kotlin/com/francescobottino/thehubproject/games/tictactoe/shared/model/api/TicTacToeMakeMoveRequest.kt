package com.francescobottino.thehubproject.games.tictactoe.shared.model.api

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeMakeMoveRequest(
    val cell: TicTacToeBoardCell,
)