package com.francescobottino.thehubproject.games.tictactoe.client.screens.game

import com.francescobottino.thehubproject.client_features.core.ui.components.AlertState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

data class GameScreenState(
    val roomId: String? = null,
    val board: TicTacToeGameState = emptyMap(),
    val userLabel: String? = null,
    val opponentState: OpponentState = OpponentState.WaitingForOpponent,
    val isUserTurn: Boolean = false,
    val isUserHost: Boolean = false,
    val roomState: TicTacToeGameRoom.State = TicTacToeGameRoom.State.WaitingForOpponent,
    val pastGamesWinners: List<TicTacToePlayerSign?> = emptyList(),
    val finishState: FinishState? = null,
    val isClosed: Boolean = false,

    val isLoading: Boolean = false,
    val dialog: AlertState? = null,
) {
    sealed interface OpponentState {
        sealed interface OpponentKnown {
            val username: String
        }
        data object WaitingForOpponent: OpponentState
        data class Connected(override val username: String): OpponentState, OpponentKnown
        data class Disconnected(override val username: String): OpponentState, OpponentKnown
        data class GameFinished(override val username: String): OpponentState, OpponentKnown
    }

    data class FinishState(
        val winnerSign: TicTacToePlayerSign? = null,
        val userWon: Boolean = false,
        val canRetry: Boolean = true,
    )

    data class ClosedState(
        val byUser: Boolean = false,
    )
}