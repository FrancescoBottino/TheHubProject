package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameState

/* todo display this info:
    val pastGamesWinners: List<TicTacToePlayer?> = emptyList(),
*/
data class GameScreenState(
    val board: TicTacToeGameState = emptyMap(),
    val userConnection: ConnectionState = ConnectionState.Connecting,
    val userLabel: String? = null,
    val opponentConnected: Boolean = false,
    val opponentLabel: String? = null,
    val isUserTurn: Boolean = false,
    val roomState: TicTacToeGameRoom.State = TicTacToeGameRoom.State.WaitingForOpponent,
    val errorDialogMessages: List<String> = emptyList(),
    val isLoading: Boolean = false,
) {
    sealed interface ConnectionState {
        data object Connecting: ConnectionState
        data object Connected: ConnectionState
        data class Disconnected(val reason: Throwable? = null): ConnectionState
    }
}