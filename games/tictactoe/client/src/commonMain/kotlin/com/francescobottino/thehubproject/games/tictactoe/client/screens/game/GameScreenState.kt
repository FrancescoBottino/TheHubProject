package com.francescobottino.thehubproject.games.tictactoe.client.screens.game

import com.francescobottino.thehubproject.client_features.core.ui.components.AlertState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

data class GameScreenState(
    val room: Room? = null,
    val isLoading: Boolean = false,
    val dialog: AlertState? = null,
) {
    data class Room(
        val roomId: String,
        val userSign: TicTacToePlayerSign,
        val board: TicTacToeGameState = emptyMap(),
        val opponentState: OpponentState = OpponentState.WaitingForOpponent,
        val isUserTurn: Boolean = false,
        val isUserHost: Boolean = false,
        val roomState: TicTacToeGameRoom.State = TicTacToeGameRoom.State.WaitingForOpponent,
        val pastGamesWinners: List<TicTacToePlayerSign?> = emptyList(),
        val shareRoomLink: String,
        val finishState: FinishState? = null,
        val isClosed: Boolean = false,
    )

    sealed interface OpponentState {
        data object WaitingForOpponent: OpponentState

        data class Known(
            val username: String,
            val sign: TicTacToePlayerSign,
            val connected: Boolean = true,
        ): OpponentState
    }

    data class FinishState(
        val winnerSign: TicTacToePlayerSign? = null,
        val userWon: Boolean = false,
        val canRetry: Boolean = true,
    )
}