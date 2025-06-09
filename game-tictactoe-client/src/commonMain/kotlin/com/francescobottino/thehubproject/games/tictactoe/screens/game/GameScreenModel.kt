package com.francescobottino.thehubproject.games.tictactoe.screens.game

import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeMoveResponseError
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class GameScreenModel(override val di: DI, private val roomId: String): StatefulScreenModel<GameScreenState, GameScreenEvent, GameScreenModelEvent>(), DIAware {
    val userRepository by instance<UserRepository>()
    val api by instance<TicTacToeApi>()

    private var roomUpdateJob: Job? = null

    private val user: User = userRepository.getCurrentUserFlow().value!!

    private val _state = MutableStateFlow(GameScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: GameScreenEvent) {
        when(event) {
            is GameScreenEvent.OnUserClickedCell -> makeMove(event.cell)
        }
    }

    fun connectToRoom() {
        _state.update {
            it.copy(
                isLoading = true,
                userConnection = GameScreenState.ConnectionState.Connecting,
            )
        }

        roomUpdateJob?.cancel()
        roomUpdateJob = api.joinRoomWebSocket(roomId)
            .distinctUntilChanged()
            .onEach { roomState ->
                val me = roomState.players.single { it.id == user.id }
                val opponent = roomState.players.singleOrNull { it.id != user.id }

                val opponentConnected = roomState.roomState !is TicTacToeGameRoom.State.WaitingForOpponent
                        && opponent != null
                        && roomState.connectedPlayerIds.contains(opponent.id)

                val opponentLabel = when {
                    opponentConnected -> "Connected"
                    roomState.roomState is TicTacToeGameRoom.State.WaitingForOpponent -> "Waiting for opponent"
                    else -> null
                }

                val isUserTurn = roomState.roomState is TicTacToeGameRoom.State.InProgress
                        && roomState.currentPlayerSign == me.sign

                _state.update { screenState ->
                    screenState.copy(
                        board = roomState.gameState,
                        isUserTurn = isUserTurn,
                        roomState = roomState.roomState,
                        userLabel = user.username,
                        opponentConnected = opponentConnected,
                        opponentLabel = opponentLabel
                    )
                }
            }
            .onStart {
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        userConnection = GameScreenState.ConnectionState.Connected,
                    )
                }
            }
            .catch { error ->
                error.printStackTrace()
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        userConnection = GameScreenState.ConnectionState.Disconnected(error),
                    )
                }
            }
            .onCompletion {
                _state.update { screenState ->
                    if(screenState.userConnection !is GameScreenState.ConnectionState.Disconnected) {
                        screenState.copy(
                            isLoading = false,
                            userConnection = GameScreenState.ConnectionState.Disconnected(),
                        )
                    } else {
                        screenState
                    }
                }
            }
            .launchIn(screenModelScope)
    }

    private fun makeMove(cell: TicTacToeBoardCell) {
        _state.update { it.copy(isLoading = true) }

        screenModelScope.launch {
            runCatching { api.makeMove(roomId, TicTacToeMakeMoveRequest(cell)) }
                .onFailure { e ->
                    _state.update { it.copy(errorDialogMessages = it.errorDialogMessages + (e.message ?: "Unknown error")) }
                }
                .onSuccess { response ->
                    response.onLeft { error ->
                        when(error) {
                            TicTacToeMakeMoveResponseError.NOT_YOUR_TURN -> TODO()
                            TicTacToeMakeMoveResponseError.ROOM_NOT_FOUND -> TODO()
                            TicTacToeMakeMoveResponseError.PLAYER_NOT_IN_ROOM -> TODO()
                            TicTacToeMakeMoveResponseError.GAME_NOT_IN_PROGRESS -> TODO()
                        }
                    }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}