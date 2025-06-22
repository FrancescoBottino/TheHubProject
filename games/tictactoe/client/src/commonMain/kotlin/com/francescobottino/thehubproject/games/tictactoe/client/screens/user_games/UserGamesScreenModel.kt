package com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.games.tictactoe.client.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.client.screens.game.GameScreen
import com.francescobottino.thehubproject.shared.model.PaginationParams
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserGamesScreenModel(
    private val navigator: Navigator,
): StatefulScreenModel<UserGamesScreenState, UserGamesScreenEvent>(), KoinComponent {
    private val api by inject<TicTacToeApi>()

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
                        it.copy(
                            rooms = response.data.map { room ->
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