package com.francescobottino.thehubproject.games.tictactoe.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacToePlayer(
    val id: String,
    val sign: TicTacToePlayerSign,
)