package com.francescobottino.thehubproject.games.tictactoe.client.screens.game

import com.francescobottino.thehubproject.client_shared.ui.components.AlertState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

data class GameScreenState(
    val roomId: String? = null,
    val board: TicTacToeGameState = emptyMap(),
    val userLabel: String? = null,
    val opponentState: OpponentState = OpponentState.WaitingForOpponent,
    val opponentLabel: String? = null,
    val isUserTurn: Boolean = false,
    val isUserHost: Boolean = false,
    val roomState: TicTacToeGameRoom.State = TicTacToeGameRoom.State.WaitingForOpponent,
    val pastGamesWinners: List<TicTacToePlayerSign?> = emptyList(),
    val finishState: FinishState? = null,

    val isLoading: Boolean = false,
    val dialog: AlertState? = null,
) {
    sealed interface OpponentState {
        data object WaitingForOpponent: OpponentState
        data class Connected(val username: String): OpponentState
        data class Disconnected(val username: String?): OpponentState
    }

    data class FinishState(
        val winnerSign: TicTacToePlayerSign? = null,
        val userWon: Boolean = false,
        val canRetry: Boolean = true,
    )
}