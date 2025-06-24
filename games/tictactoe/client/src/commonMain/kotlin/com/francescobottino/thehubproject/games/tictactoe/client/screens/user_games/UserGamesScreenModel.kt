package com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.client_shared.usecase.GetRelativeTimeUseCase.relativeTime
import com.francescobottino.thehubproject.games.tictactoe.client.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.client.screens.game.GameScreen
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.shared.model.PaginationParams
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserGamesScreenModel(
    private val navigator: Navigator,
): StatefulScreenModel<UserGamesScreenState, UserGamesScreenEvent>(), KoinComponent {
    private val api by inject<TicTacToeApi>()
    private val userRepository by inject<UserRepository>()

    private var refreshJob: Job? = null

    private val _state = MutableStateFlow(UserGamesScreenState())
    override val state = _state.asStateFlow()

    init {
        refresh()
    }

    override fun onEvent(event: UserGamesScreenEvent) {
        when(event) {
            is UserGamesScreenEvent.OnDialogClosed -> _state.update { it.copy(error = null) }
            is UserGamesScreenEvent.OnRoomClicked -> navigator.push(GameScreen(event.room.id))
        }
    }

    //Uses only first page - implement pagination
    private fun refresh() {
        if(refreshJob?.isActive == true) return

        _state.update { it.copy(isLoading = true) }
        refreshJob = screenModelScope.launch {
            runCatching { api.myRooms(PaginationParams()) }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message ?: "Unknown error") }
                }
                .onSuccess { response ->
                    _state.update {
                        val user = userRepository.getCurrentUser()!!

                        val rooms = response.data.map { room ->
                            val userSign = room.players.single { player -> player.user.id == user.id }.sign
                            val isUserTurn = room.currentPlayerSign == userSign

                            UserGamesScreenState.Room(
                                id = room.id,
                                lastUpdate = room.lastUpdate.relativeTime(),
                                state = when (room.roomState) {
                                    is TicTacToeGameRoom.State.WaitingForOpponent -> UserGamesScreenState.Room.State.Waiting
                                    is TicTacToeGameRoom.State.InProgress -> UserGamesScreenState.Room.State.Playing(
                                        if(isUserTurn) "Your turn" else "Opponent's turn"
                                    )
                                    is TicTacToeGameRoom.State.Finished -> {
                                        val winner = (room.roomState as TicTacToeGameRoom.State.Finished).winner
                                        UserGamesScreenState.Room.State.Finished(
                                            when (winner) {
                                                userSign -> "You won"
                                                null -> "It's a draw"
                                                else -> "You lost"
                                            }
                                        )
                                    }
                                    is TicTacToeGameRoom.State.Closed -> UserGamesScreenState.Room.State.Closed
                                },
                                userIsHost = room.hostPlayer.user.id == user.id,
                                userTurn = isUserTurn,
                                userSign = userSign,
                                opponentName = room.players.singleOrNull { player -> player.user.id != user.id }?.user?.username,
                                winners = room.pastGamesWinners.groupBy { sign -> sign }.mapValues { (_, list) -> list.size }
                            )
                        }

                        it.copy(rooms = rooms)
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}