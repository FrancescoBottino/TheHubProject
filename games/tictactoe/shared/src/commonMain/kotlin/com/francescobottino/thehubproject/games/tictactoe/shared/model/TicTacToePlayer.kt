package com.francescobottino.thehubproject.games.tictactoe.shared.model

import com.francescobottino.thehubproject.shared.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToePlayer(
    val user: UserResponse,
    val sign: TicTacToePlayerSign,
)