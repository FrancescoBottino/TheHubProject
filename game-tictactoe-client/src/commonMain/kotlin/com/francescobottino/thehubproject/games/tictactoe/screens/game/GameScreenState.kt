package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign

data class GameScreenState(
    val roomId: String = "",
    val board: TicTacToeGameState = emptyMap(),
    val userConnection: ConnectionState = ConnectionState.Connecting,
    val userLabel: String? = null,
    val opponentConnected: Boolean = false,
    val opponentLabel: String? = null,
    val isUserTurn: Boolean = false,
    val roomState: TicTacToeGameRoom.State = TicTacToeGameRoom.State.WaitingForOpponent,
    val errorDialogMessages: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val pastGamesWinners: List<TicTacToePlayerSign?> = emptyList(),
) {
    sealed interface ConnectionState {
        data object Connecting: ConnectionState
        data object Connected: ConnectionState
        data class Disconnected(val reason: Throwable? = null): ConnectionState
    }
}