package com.francescobottino.thehubproject.games.tictactoe.model.api

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeMakeRoomRequest(
    val chosenSign: TicTacToePlayerSign,
    val startingSign: TicTacToePlayerSign,
)