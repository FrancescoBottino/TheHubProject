package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign

data class GameScreenState(
    val roomId: String = "",
    val board: TicTacToeGameState = emptyMap(),
    val userLabel: String? = null,
    val opponentConnected: Boolean = false,
    val opponentLabel: String? = null,
    val isUserTurn: Boolean = false,
    val roomState: TicTacToeGameRoom.State = TicTacToeGameRoom.State.WaitingForOpponent,
    val pastGamesWinners: List<TicTacToePlayerSign?> = emptyList(),

    val isLoading: Boolean = false,
    val dialog: Dialog? = null,
) {
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
}