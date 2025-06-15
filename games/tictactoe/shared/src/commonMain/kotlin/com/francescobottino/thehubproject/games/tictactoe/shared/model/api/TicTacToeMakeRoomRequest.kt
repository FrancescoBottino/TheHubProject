package com.francescobottino.thehubproject.games.tictactoe.shared.model.api

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeMakeRoomRequest(
    val chosenSign: TicTacToePlayerSign,
    val startingSign: TicTacToePlayerSign,
)