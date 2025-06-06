package com.francescobottino.thehubproject.games.tictactoe.model

import kotlinx.serialization.Serializable

@Serializable
enum class TicTacToePlayerSign {
    X, O;

    fun otherSign(): TicTacToePlayerSign = if (this == X) O else X
}