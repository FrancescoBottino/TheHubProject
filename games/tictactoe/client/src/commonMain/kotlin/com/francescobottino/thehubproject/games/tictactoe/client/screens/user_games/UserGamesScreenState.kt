package com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

data class UserGamesScreenState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    data class Room(
        val id: String,
        val lastUpdate: String,
        val state: State,
        val userIsHost: Boolean,
        val userTurn: Boolean,
        val userSign: TicTacToePlayerSign,
        val opponentName: String?,
        val winners: Map<TicTacToePlayerSign?, Int>,
    ) {
        sealed class State {
            abstract val label: String
            @get:Composable
            abstract val color: Color

            data object Waiting: State() {
                override val label: String
                    get() = "Waiting"
                override val color: Color
                    @Composable
                    get() = MaterialTheme.colorScheme.secondary
            }

            data class Playing(override val label: String): State() {
                override val color: Color
                    @Composable
                    get() = MaterialTheme.colorScheme.primary
            }

            data class Finished(override val label: String): State() {
                override val color: Color
                    @Composable
                    get() = MaterialTheme.colorScheme.tertiary
            }

            data object Closed: State() {
                override val label: String
                    get() = "Closed"
                override val color: Color
                    @Composable
                    get() = MaterialTheme.colorScheme.error
            }
        }
    }
}