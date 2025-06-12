package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign

data class GameScreenState(
    val roomId: String = "",
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
    val dialog: Dialog? = null,
) {
    sealed interface OpponentState {
        data object WaitingForOpponent: OpponentState
        data class Connected(val username: String): OpponentState
        data class Disconnected(val username: String?): OpponentState
    }

    data class Dialog(
        val title: String,
        val message: String? = null,
        val dismissable: Boolean = true,
        val onConfirm: Action? = null,
        val onDismiss: Action? = null,
    ) {
        data class Action(
            val label: String,
            val event: GameScreenEvent,
        )
    }

    data class FinishState(
        val winnerSign: TicTacToePlayerSign? = null,
        val userWon: Boolean = false,
        val canRetry: Boolean = true,
    )
}