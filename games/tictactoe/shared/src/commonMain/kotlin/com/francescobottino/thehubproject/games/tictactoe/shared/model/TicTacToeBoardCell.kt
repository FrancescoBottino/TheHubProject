package com.francescobottino.thehubproject.games.tictactoe.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeBoardCell(val r: Int, val c: Int) {
    init {
        require(r in 0..2 && c in 0..2) { "Row and column must be between 0 and 2" }
    }
}