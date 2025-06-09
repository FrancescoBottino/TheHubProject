package com.francescobottino.thehubproject.games.tictactoe.model

import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeMakeRoomRequest(
    val chosenSign: TicTacToePlayerSign,
    val startingSign: TicTacToePlayerSign,
)