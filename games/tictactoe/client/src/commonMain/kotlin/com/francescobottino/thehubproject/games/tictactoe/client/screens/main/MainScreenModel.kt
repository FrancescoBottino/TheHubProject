package com.francescobottino.thehubproject.games.tictactoe.client.screens.main

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.games.tictactoe.client.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.client.screens.game.GameScreen
import com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games.UserGamesScreen
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeJoinRoomResponseError
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeMakeRoomRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainScreenModel(
    private val navigator: Navigator,
): StatefulScreenModel<MainScreenState, MainScreenEvent>(), KoinComponent {
    val api by inject<TicTacToeApi>()

    private val _state = MutableStateFlow(MainScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnSearchedRoomIdChanged -> _state.update { it.copy(searchedRoomId = event.newSearchedRoomId) }
            is MainScreenEvent.OnCreateRoom -> createRoom()
            is MainScreenEvent.OnJoinRoom -> joinRoom()
            is MainScreenEvent.OnSeeMyGames -> navigator.push(UserGamesScreen)
            is MainScreenEvent.OnDialogClosed -> _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue.drop(1)) }
        }
    }

    private fun createRoom() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            api.makeRoom(
                TicTacToeMakeRoomRequest(
                    chosenSign = TicTacToePlayerSign.X,
                    startingSign = TicTacToePlayerSign.X,
                )
            ).let {
                navigator.push(GameScreen(it))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun joinRoom() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val id = state.value.searchedRoomId

            runCatching { api.joinRoom(id) }
                .onFailure { e ->
                    _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue + (e.message ?: "Unknown error")) }
                }
                .onSuccess { response ->
                    response.onLeft { error ->
                        when(error) {
                            TicTacToeJoinRoomResponseError.PLAYER_ALREADY_IN_ROOM -> navigator.push(GameScreen(id))
                            TicTacToeJoinRoomResponseError.ROOM_NOT_FOUND -> _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue + "Room not found. Please try again with a valid room ID.") }
                            TicTacToeJoinRoomResponseError.ROOM_ALREADY_FULL -> _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue + "Room already full.") }
                        }
                    }.onRight {
                        navigator.push(GameScreen(id))
                    }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}