package com.francescobottino.thehubproject.games.tictactoe.screens.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeJoinRoomResponseError
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeRoomRequest
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.screens.game.GameScreen
import com.francescobottino.thehubproject.games.tictactoe.screens.user_games.UserGamesScreen
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class MainScreenModel(
    override val di: DI,
): StatefulScreenModel<MainScreenState, MainScreenEvent, MainScreenModelEvent>(MainScreenState()), DIAware {
    val api by di.instance<TicTacToeApi>()

    override fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnSearchedRoomIdChanged -> _state.update { it.copy(searchedRoomId = event.newSearchedRoomId) }
            is MainScreenEvent.OnCreateRoom -> createRoom()
            is MainScreenEvent.OnJoinRoom -> joinRoom()
            is MainScreenEvent.OnSeeMyGames -> _screenModelEventsFlow.tryEmit(MainScreenModelEvent.Navigate(UserGamesScreen))
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
                _screenModelEventsFlow.tryEmit(MainScreenModelEvent.Navigate(GameScreen(it)))
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
                            TicTacToeJoinRoomResponseError.PLAYER_ALREADY_IN_ROOM -> { _screenModelEventsFlow.tryEmit(MainScreenModelEvent.Navigate(GameScreen(id))) }
                            TicTacToeJoinRoomResponseError.ROOM_NOT_FOUND -> _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue + "Room not found. Please try again with a valid room ID.") }
                            TicTacToeJoinRoomResponseError.ROOM_ALREADY_FULL -> _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue + "Room already full.") }
                        }
                    }.onRight {
                        _screenModelEventsFlow.tryEmit(MainScreenModelEvent.Navigate(GameScreen(id)))
                    }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}