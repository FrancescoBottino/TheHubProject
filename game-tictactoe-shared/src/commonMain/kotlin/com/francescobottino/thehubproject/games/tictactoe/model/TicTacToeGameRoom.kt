package com.francescobottino.thehubproject.games.tictactoe.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TicTacToeGameRoom(
    val id: String,
    val hostPlayer: TicTacToePlayer,
    val opponentPlayer: TicTacToePlayer? = null,
    val gameState: TicTacToeGameState = emptyMap(),
    val pastGamesWinners: List<TicTacToePlayer?> = emptyList(),
    val currentPlayerSign: TicTacToePlayerSign,
    val roomState: State = State.WaitingForOpponent,
    val connectedPlayerIds: List<String> = emptyList(),
    val lastUpdate: Instant = Clock.System.now(),
) {
    val players: List<TicTacToePlayer> = listOfNotNull(hostPlayer, opponentPlayer)

    @Serializable
    sealed interface State {
        @Serializable
        data object WaitingForOpponent: State
        @Serializable
        data object InProgress: State
        @Serializable
        data class Finished(val winner: TicTacToePlayer?): State // if winner is null, it's a tie
        @Serializable
        data class Closed(val byPlayer: TicTacToePlayer): State
    }
}