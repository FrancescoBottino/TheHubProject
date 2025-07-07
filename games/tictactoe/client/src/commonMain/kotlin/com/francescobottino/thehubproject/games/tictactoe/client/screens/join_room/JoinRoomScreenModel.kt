package com.francescobottino.thehubproject.games.tictactoe.client.screens.join_room

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_features.core.screens.StatefulScreenModel
import com.francescobottino.thehubproject.games.tictactoe.client.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.client.screens.game.GameScreen
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeJoinRoomResponseError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class JoinRoomScreenModel(
    private val navigator: Navigator,
    private val roomId: String,
): StatefulScreenModel<JoinRoomScreenState, JoinRoomScreenEvent>(), KoinComponent {
    val api by inject<TicTacToeApi>()

    private var job: Job? = null
    private val _state = MutableStateFlow(JoinRoomScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: JoinRoomScreenEvent) {
        when(event) {
            is JoinRoomScreenEvent.Retry -> joinRoom()
        }
    }

    init {
        joinRoom()
    }

    private fun joinRoom() {
        _state.update { it.copy(error = null) }
        job?.cancel()
        job = screenModelScope.launch {
            runCatching { api.joinRoom(roomId) }
                .onFailure { e ->
                    _state.update { it.copy(error = "There was an error while trying to communicate with the server.") }
                }
                .onSuccess { response ->
                    response.onLeft { error ->
                        when(error) {
                            TicTacToeJoinRoomResponseError.PLAYER_ALREADY_IN_ROOM -> navigator.replace(GameScreen(roomId))
                            TicTacToeJoinRoomResponseError.ROOM_NOT_FOUND -> _state.update { it.copy(error = "Room not found. Please try again with a valid room ID.") }
                            TicTacToeJoinRoomResponseError.ROOM_ALREADY_FULL -> _state.update { it.copy(error = "Room already full.") }
                        }
                    }.onRight {
                        navigator.replace(GameScreen(roomId))
                    }
                }
        }
    }
}