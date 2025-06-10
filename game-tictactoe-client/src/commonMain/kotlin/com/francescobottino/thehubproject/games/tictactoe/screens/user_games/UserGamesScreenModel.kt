package com.francescobottino.thehubproject.games.tictactoe.screens.user_games

import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.screens.game.GameScreen
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class UserGamesScreenModel(override val di: DI): StatefulScreenModel<UserGamesScreenState, UserGamesScreenEvent, UserGamesScreenModelEvent>(), DIAware {
    private val api by instance<TicTacToeApi>()

    private var refreshJob: Job? = null

    private val _state = MutableStateFlow(UserGamesScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: UserGamesScreenEvent) {
        when(event) {
            is UserGamesScreenEvent.OnDialogClosed -> _state.update { it.copy(error = null) }
            is UserGamesScreenEvent.OnRoomClicked -> _screenModelEventsFlow.tryEmit(UserGamesScreenModelEvent.Navigate(GameScreen(event.room.id)))
        }
    }

    fun firstLoad() {
        if(refreshJob?.isActive == true) return

        _state.update { it.copy(isLoading = true) }
        refreshJob = screenModelScope.launch {
            runCatching { api.myRooms() }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message ?: "Unknown error") }
                }
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            rooms = response.map { room ->
                                UserGamesScreenState.Room(
                                    id = room.id,
                                    lastUpdate = room.lastUpdate.toLocalDateTime(TimeZone.currentSystemDefault())
                                )
                            },
                            isLoading = false
                        )
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}