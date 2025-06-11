package com.francescobottino.thehubproject.games.tictactoe.screens.game

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeMoveResponseError
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import com.francescobottino.thehubproject.mainJson
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class GameScreenModel(
    override val di: DI,
    private val navigator: Navigator,
    private val roomId: String,
): StatefulScreenModel<GameScreenState, GameScreenEvent>(), DIAware {
    val userRepository by instance<UserRepository>()
    val api by instance<TicTacToeApi>()

    private var roomUpdateJob: Job? = null
    private var ws: DefaultClientWebSocketSession? = null

    private val user: User = userRepository.getCurrentUserFlow().value!!

    private val _state = MutableStateFlow(GameScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: GameScreenEvent) {
        when(event) {
            is GameScreenEvent.OnBoardCellClicked -> makeMove(event.cell)
            is GameScreenEvent.OnDismissDialog -> _state.update { it.copy(dialog = null) }
            is GameScreenEvent.OnDialogActionConnectToRoom -> {
                _state.update { it.copy(dialog = null) }
                connectToRoom()
            }
            is GameScreenEvent.OnCloseScreen -> navigator.pop()
            is GameScreenEvent.OnCloseRoom -> TODO()
        }
    }

    fun connectToRoom() {
        _state.update { it.copy(isLoading = true) }

        roomUpdateJob?.cancel()
        roomUpdateJob = screenModelScope.launch {
            val webSocket = runCatching { api.joinRoomWebSocket(roomId) }
                .onFailure { error ->
                    error.printStackTrace()
                    _state.update { screenState ->
                        screenState.copy(
                            isLoading = false,
                            dialog = GameScreenState.Dialog(
                                title = "Error connecting to the room",
                                message = error.message ?: "Unknown error",
                                dismissable = false,
                                onConfirm = GameScreenState.Dialog.Action("Retry", GameScreenEvent.OnDialogActionConnectToRoom),
                                onDismiss = GameScreenState.Dialog.Action("Leave", GameScreenEvent.OnCloseScreen),
                            )
                        )
                    }
                }
                .onSuccess { it ->
                    ws = it
                }
                .getOrNull()
                ?: return@launch

            try {
                for (frame in webSocket.incoming) {
                    if (frame is Frame.Text) {
                        val update = runCatching {
                            mainJson.decodeFromString<TicTacToeGameRoom>(frame.readText())
                        }.getOrNull()

                        if(update != null) {
                            val me = update.players.single { it.id == user.id }
                            val opponent = update.players.singleOrNull { it.id != user.id }

                            val isUserHost = update.hostPlayer.id == user.id

                            val opponentConnected = update.roomState !is TicTacToeGameRoom.State.WaitingForOpponent
                                    && opponent != null
                                    && update.connectedPlayerIds.contains(opponent.id)

                            val opponentLabel = when {
                                opponentConnected -> "Connected"
                                update.roomState is TicTacToeGameRoom.State.WaitingForOpponent -> "Waiting for opponent"
                                else -> "Disconnected"
                            }

                            val isUserTurn = update.roomState is TicTacToeGameRoom.State.InProgress
                                    && update.currentPlayerSign == me.sign

                            val finishState = if (update.roomState is TicTacToeGameRoom.State.Finished) {
                                val winner = (update.roomState as TicTacToeGameRoom.State.Finished).winner
                                GameScreenState.FinishState(
                                    winnerSign = winner?.sign,
                                    userWon = winner?.id == me.id,
                                    canRetry = isUserHost,
                                )
                            } else {
                                null
                            }

                            _state.update { screenState ->
                                screenState.copy(
                                    isLoading = false,

                                    roomId = roomId,
                                    board = update.gameState,
                                    isUserTurn = isUserTurn,
                                    isUserHost = isUserHost,
                                    roomState = update.roomState,
                                    userLabel = user.username,
                                    opponentConnected = opponentConnected,
                                    opponentLabel = opponentLabel,
                                    finishState = finishState,
                                )
                            }
                        }
                    } else if (frame is Frame.Close) {
                        _state.update { screenState ->
                            screenState.copy(
                                isLoading = false,
                                dialog = GameScreenState.Dialog(
                                    title = "Disconnected",
                                    message = "You have been disconnected from the room.",
                                    dismissable = false,
                                    onConfirm = GameScreenState.Dialog.Action("Close screen", GameScreenEvent.OnCloseScreen),
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        dialog = GameScreenState.Dialog(
                            title = "Error",
                            message = e.message ?: "Unknown error",
                            dismissable = false,
                            onConfirm = GameScreenState.Dialog.Action("Close screen", GameScreenEvent.OnCloseScreen),
                        )
                    )
                }
            }
        }
    }

    private fun makeMove(cell: TicTacToeBoardCell) {
        _state.update { it.copy(isLoading = true) }

        screenModelScope.launch {
            runCatching { api.makeMove(roomId, TicTacToeMakeMoveRequest(cell)) }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            dialog = GameScreenState.Dialog(
                                title = "Error",
                                message = e.message ?: "Unknown error",
                                dismissable = true,
                                onConfirm = GameScreenState.Dialog.Action("OK", GameScreenEvent.OnDismissDialog),
                            ),
                        )
                    }
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

    override fun onDispose() {
        roomUpdateJob?.cancel()
        screenModelScope.launch(NonCancellable) { ws?.close() }
        super.onDispose()
    }
}