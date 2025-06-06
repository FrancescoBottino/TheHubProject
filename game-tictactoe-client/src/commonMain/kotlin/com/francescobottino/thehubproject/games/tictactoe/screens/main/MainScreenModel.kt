package com.francescobottino.thehubproject.games.tictactoe.screens.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.games.tictactoe.api.TicTacToeMakeRoomRequest
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
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
                _screenModelEventsFlow.tryEmit(MainScreenModelEvent.NavigateToGameScreen(it))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun joinRoom() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val id = state.value.searchedRoomId
            api.joinRoom(id).let {
                _screenModelEventsFlow.tryEmit(MainScreenModelEvent.NavigateToGameScreen(id))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}