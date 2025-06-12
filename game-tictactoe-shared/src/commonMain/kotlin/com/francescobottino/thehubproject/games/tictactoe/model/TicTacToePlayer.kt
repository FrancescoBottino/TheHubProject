package com.francescobottino.thehubproject.games.tictactoe.model

import com.francescobottino.thehubproject.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToePlayer(
    val user: UserResponse,
    val sign: TicTacToePlayerSign,
)