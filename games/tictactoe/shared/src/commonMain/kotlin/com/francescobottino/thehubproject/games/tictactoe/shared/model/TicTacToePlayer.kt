package com.francescobottino.thehubproject.games.tictactoe.shared.model

import com.francescobottino.thehubproject.shared_features.core.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToePlayer(
    val user: UserResponse,
    val sign: TicTacToePlayerSign,
)