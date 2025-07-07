package com.francescobottino.thehubproject.games.tictactoe.client.screens.game

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.model.User
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.client_shared.ui.components.AlertState
import com.francescobottino.thehubproject.client_shared.usecase.CopyToClipboardUseCase
import com.francescobottino.thehubproject.games.tictactoe.client.network.TicTacToeApi
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeMakeMoveResponseError
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeRestartGameResponseError
import com.francescobottino.thehubproject.shared.mainJson
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GameScreenModel(
    private val navigator: Navigator,
    private val roomId: String,
): StatefulScreenModel<GameScreenState, GameScreenEvent>(), KoinComponent {
    val userRepository by inject<UserRepository>()
    val copyToClipboard by inject<CopyToClipboardUseCase>()
    val api by inject<TicTacToeApi>()

    private var roomUpdateJob: Job? = null
    private var ws: DefaultClientWebSocketSession? = null

    private val user: User = userRepository.getCurrentUserFlow().value!!

    private val _state = MutableStateFlow(GameScreenState(isLoading = true))
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
            is GameScreenEvent.OnRetry -> restart()
            is GameScreenEvent.OnCopyRoomId -> screenModelScope.launch { copyToClipboard(roomId) }
        }
    }

    fun connectToRoom() {
        _state.update { it.copy(isLoading = true) }

        Napier.d(tag = "TicTacToes GameScreenModel") { "Attempting ws connection to room id $roomId" }

        roomUpdateJob?.cancel()
        roomUpdateJob = screenModelScope.launch {
            val webSocket = runCatching { api.joinRoomWebSocket(roomId) }
                .onFailure { error ->
                    Napier.d(tag = "TicTacToes GameScreenModel") { "failed to connect to room $roomId, error: $error" }
                    error.printStackTrace()
                    _state.update { screenState ->
                        screenState.copy(
                            isLoading = false,
                            dialog = AlertState(
                                title = "Error connecting to the room",
                                message = error.message ?: "Unknown error",
                                dismissable = false,
                                primaryAction = AlertState.Action(
                                    label = "Retry",
                                    onClick = { onEvent(GameScreenEvent.OnDialogActionConnectToRoom) }
                                ),
                                secondaryAction = AlertState.Action(
                                    label = "Leave",
                                    onClick = { onEvent(GameScreenEvent.OnCloseScreen) },
                                )
                            )
                        )
                    }
                }
                .onSuccess { it ->
                    Napier.d(tag = "TicTacToes GameScreenModel") { "connected to room $roomId" }
                    ws = it
                }
                .getOrNull()
                ?: return@launch

            try {
                Napier.d(tag = "TicTacToes GameScreenModel") { "ws incoming channel opened" }
                listenToWebsocketUpdates(webSocket)
                Napier.d(tag = "TicTacToes GameScreenModel") { "ws incoming channel closed" }
                val closeReason = webSocket.closeReason.await()

                when(closeReason?.code) {
                    CloseReason.Codes.CANNOT_ACCEPT.code -> _state.update { screenState ->
                        screenState.copy(
                            isLoading = false,
                            dialog = AlertState(
                                title = "Connection refused",
                                message = "Cannot connect to the room.\n"+closeReason.message,
                                dismissable = false,
                                primaryAction = AlertState.Action(
                                    label = "Close screen",
                                    onClick = { onEvent(GameScreenEvent.OnCloseScreen) }
                                )
                            )
                        )
                    }
                    else -> _state.update { screenState ->
                        screenState.copy(
                            isLoading = false,
                            dialog = AlertState(
                                title = "Disconnected",
                                message = "You have been disconnected from the room.",
                                dismissable = false,
                                primaryAction = AlertState.Action(
                                    label = "Close screen",
                                    onClick = { onEvent(GameScreenEvent.OnCloseScreen) }
                                )
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Napier.d(tag = "TicTacToes GameScreenModel") { "ws exception $e" }
                e.printStackTrace()
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        dialog = AlertState(
                            title = "Error",
                            message = e.message ?: "Unknown error",
                            dismissable = false,
                            primaryAction = AlertState.Action(
                                label = "Close screen",
                                onClick = { onEvent(GameScreenEvent.OnCloseScreen) }
                            ),
                        )
                    )
                }
            }
        }
    }

    private suspend fun listenToWebsocketUpdates(webSocket: DefaultClientWebSocketSession) {
        for (frame in webSocket.incoming) {
            Napier.d(tag = "TicTacToes GameScreenModel") { "incoming frame $frame" }

            when (frame) {
                is Frame.Text -> {
                    Napier.d(tag = "TicTacToes GameScreenModel") { "frame is text, decoding as TicTacToeGameRoom" }

                    val update = runCatching {
                        mainJson.decodeFromString<TicTacToeGameRoom>(frame.readText())
                    }.getOrNull()

                    if(update != null) {
                        parseUpdate(update)
                    }
                }

                is Frame.Close -> {
                    Napier.d(tag = "TicTacToes GameScreenModel") { "frame is close" }
                    return
                }

                else -> {
                    Napier.d(tag = "TicTacToes GameScreenModel") { "frame ignored" }
                }
            }
        }
    }

    private fun parseUpdate(update: TicTacToeGameRoom) {
        val me = update.players.single { it.user.id == user.id }
        val opponent = update.players.singleOrNull { it.user.id != user.id }

        val isUserHost = update.hostPlayer.user.id == user.id

        val opponentConnected = update.roomState !is TicTacToeGameRoom.State.WaitingForOpponent
                && opponent != null
                && update.connectedPlayerIds.contains(opponent.user.id)

        val opponentState = when {
            update.roomState is TicTacToeGameRoom.State.WaitingForOpponent -> GameScreenState.OpponentState.WaitingForOpponent
            opponentConnected -> GameScreenState.OpponentState.Connected(opponent.user.username)
            else -> GameScreenState.OpponentState.Disconnected(opponent?.user?.username)
        }

        val isUserTurn = update.roomState is TicTacToeGameRoom.State.InProgress
                && update.currentPlayerSign == me.sign

        val finishState = if (update.roomState is TicTacToeGameRoom.State.Finished) {
            val winner = (update.roomState as TicTacToeGameRoom.State.Finished).winner
            GameScreenState.FinishState(
                winnerSign = winner,
                userWon = winner == me.sign,
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
                opponentState = opponentState,
                finishState = finishState,
            )
        }
    }

    private fun makeMove(cell: TicTacToeBoardCell) {
        _state.update { it.copy(isLoading = true) }

        screenModelScope.launch {
            runCatching { api.makeMove(roomId, TicTacToeMakeMoveRequest(cell)) }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            dialog = AlertState(
                                title = "Error",
                                message = e.message ?: "Unknown error",
                                dismissable = true,
                                primaryAction = AlertState.Action(
                                    label = "OK",
                                    onClick = { onEvent(GameScreenEvent.OnDismissDialog) }
                                ),
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
                            TicTacToeMakeMoveResponseError.CELL_ALREADY_OCCUPIED -> TODO()
                        }
                    }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun restart() {
        _state.update { it.copy(isLoading = true) }

        screenModelScope.launch {
            runCatching { api.restart(roomId) }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            dialog = AlertState(
                                title = "Error",
                                message = e.message ?: "Unknown error",
                                dismissable = true,
                                primaryAction = AlertState.Action(
                                    label = "OK",
                                    onClick = { onEvent(GameScreenEvent.OnDismissDialog) }
                                ),
                            ),
                        )
                    }
                }
                .onSuccess { response ->
                    response.onLeft { error ->
                        when(error) {
                            TicTacToeRestartGameResponseError.ROOM_NOT_FOUND -> TODO()
                            TicTacToeRestartGameResponseError.NOT_THE_HOST -> TODO()
                            TicTacToeRestartGameResponseError.GAME_NOT_FINISHED -> TODO()
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