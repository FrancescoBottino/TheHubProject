package com.francescobottino.thehubproject.games.tictactoe.client.screens.game

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertCardOverlay
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertState
import com.francescobottino.thehubproject.client_features.core.ui.components.LoadingCardOverlay
import com.francescobottino.thehubproject.games.tictactoe.client.ui.components.PastWinnersRowFull
import com.francescobottino.thehubproject.games.tictactoe.client.ui.components.SignIcon
import com.francescobottino.thehubproject.games.tictactoe.client.ui.images.TicTacToeCrown
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import compose.icons.FeatherIcons
import compose.icons.feathericons.Copy
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class GameScreen(private val roomId: String): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel by remember { mutableStateOf(GameScreenModel(navigator, roomId)) }

        val state by screenModel.state.collectAsState()

        GameScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )

        DisposableEffect(Unit) {
            screenModel.connectToRoom()
            onDispose {
                screenModel.onDispose()
            }
        }
    }
}

@Composable
private fun GameScreenContent(
    state: GameScreenState,
    onEvent: (GameScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if(state.room != null) {
        Column(
            modifier = modifier
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                UserLabel(state.room.userSign)

                OpponentConnectionStatus(
                    opponentState = state.room.opponentState,
                )
            }

            Spacer(Modifier.height(30.dp))

            TurnIndicator(state = state.room)

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .requiredWidthIn(max = 400.dp)
                    .padding(horizontal = 16.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp), clip = true)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .aspectRatio(1f, matchHeightConstraintsFirst = false)
            ) {
                Board(
                    state = state.room.board,
                    isUserTurn = state.room.isUserTurn,
                    onMove = { cell -> onEvent(GameScreenEvent.OnBoardCellClicked(cell))},
                    modifier = Modifier.fillMaxSize()
                )

                state.room.finishState?.let {
                    FinishDialog(
                        finishState = it,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }


            Spacer(Modifier.height(40.dp))

            if(state.room.isClosed) {
                RoomClosedInfoCard()
            } else {
                RoomIdCard(
                    roomId = state.room.roomId,
                    onClick = { onEvent(GameScreenEvent.OnCopyRoomId) }
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { onEvent(GameScreenEvent.OnCloseRoom) },
                ) {
                    Text("Close room")
                }
            }

            Spacer(Modifier.height(48.dp))

            if(state.room.pastGamesWinners.isNotEmpty()) {
                PastWinnersRowFull(
                    winners = state.room.pastGamesWinners,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    LoadingCardOverlay(state.room == null || state.isLoading)

    state.dialog?.let { dialogState ->
        AlertCardOverlay(
            state = dialogState,
            onDismissRequest = { onEvent(GameScreenEvent.OnDismissDialog) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun OpponentConnectionStatus(
    opponentState: GameScreenState.OpponentState,
    modifier: Modifier = Modifier,
) {
    val color = when(opponentState) {
        is GameScreenState.OpponentState.WaitingForOpponent -> MaterialTheme.colorScheme.onSurface
        is GameScreenState.OpponentState.Known -> when(opponentState.connected) {
            false -> MaterialTheme.colorScheme.error
            true -> MaterialTheme.colorScheme.primary
        }
    }

    val label = when(opponentState) {
        is GameScreenState.OpponentState.WaitingForOpponent -> "Waiting for opponent..."
        is GameScreenState.OpponentState.Known -> opponentState.username
    }

    val shape = RoundedCornerShape(50)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, clip = true)
            .clip(shape)
            .border(width = 1.dp, color = color, shape = shape)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 12.dp),
    ) {
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when(opponentState) {
            is GameScreenState.OpponentState.WaitingForOpponent -> {
                Box(modifier = Modifier.requiredSize(16.dp).clip(CircleShape).background(color)) {
                    CircularProgressIndicator()
                }
            }

            is GameScreenState.OpponentState.Known -> {
                SignIcon(
                    sign = opponentState.sign,
                    modifier = Modifier.requiredSize(20.dp)
                )
            }
        }
    }
}

@Composable
private fun UserLabel(
    userSign: TicTacToePlayerSign,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, clip = true)
            .clip(shape)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface, shape = shape)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 6.dp, horizontal = 12.dp),
    ) {
        SignIcon(
            sign = userSign,
            modifier = Modifier.requiredSize(20.dp)
        )
        Text(text = "You")
    }
}

@Composable
private fun TurnIndicator(
    state: GameScreenState.Room,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            when {
                state.roomState is TicTacToeGameRoom.State.InProgress -> when {
                    state.isUserTurn -> "Your turn"
                    else -> "Opponent's turn"
                }
                else -> ""
            }
        )
    }
}

@Composable
private fun Board(
    state: TicTacToeGameState,
    isUserTurn: Boolean,
    onMove: (TicTacToeBoardCell) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        (0..2).forEach { rowIndex ->
            if(rowIndex != 0) {
                HorizontalDivider()
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                (0..2).forEach { colIndex ->
                    if(colIndex != 0) {
                        VerticalDivider()
                    }

                    val cell = TicTacToeBoardCell(rowIndex, colIndex)
                    val sign = state[cell]

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = sign == null && isUserTurn) { onMove(cell) },
                        contentAlignment = Alignment.Center,
                    ) {
                        sign?.let {
                            SignIcon(
                                sign = it,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(1f)
                                    .padding(8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinishDialog(
    finishState: GameScreenState.FinishState,
    onEvent: (GameScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(18.dp)
                .clip(RoundedCornerShape(12.dp))
                .shadow(elevation = 12.dp, RoundedCornerShape(12.dp))
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .requiredWidthIn(min = 160.dp),
        ) {
            Text(
                text = "Game Over!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Box(
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .requiredSize(96.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                ) {
                    SignIcon(
                        sign = finishState.winnerSign,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                if(finishState.winnerSign != null) {
                    Box(
                        modifier = Modifier.requiredSize(32.dp),
                    ) {
                        Image(
                            TicTacToeCrown,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(12f)
                                .offset(y = (-40).dp),
                        )
                    }
                }
            }

            Text(
                text = when {
                    finishState.winnerSign == null -> "It's a tie"
                    finishState.userWon -> "You won!"
                    else -> "You lost..."
                }
            )

            Spacer(Modifier.height(8.dp))

            if(finishState.canRetry) {
                Button(
                    onClick = { onEvent(GameScreenEvent.OnRetry) },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Retry")
                }
            } else {
                Text(
                    text = "You can now wait for the host to restart the match or close the room.",
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun RoomIdCard(
    roomId: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20)
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "You can share this room id to your opponent: ",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentSize()
                .shadow(elevation = 12.dp, shape = shape, clip = true)
                .clip(shape)
                .clickable(onClick = onClick)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.onPrimary, shape = shape)
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(6.dp),
        ) {
            SelectionContainer {
                Text(
                    text = roomId,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Icon(
                FeatherIcons.Copy,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun RoomClosedInfoCard(
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20)
    Text(
        text = "The room is closed",
        color = MaterialTheme.colorScheme.onError,
        modifier = modifier
            .wrapContentSize()
            .shadow(elevation = 12.dp, shape = shape, clip = true)
            .clip(shape)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onError, shape = shape)
            .background(color = MaterialTheme.colorScheme.error)
            .padding(6.dp),
    )
}

private class GameScreenContentPreviewProvider: PreviewParameterProvider<GameScreenState> {
    override val values: Sequence<GameScreenState> = sequence {
        var state = GameScreenState(
            room = GameScreenState.Room(
                userSign = TicTacToePlayerSign.X,
                roomId = "2f9f6008-8b10-4d56-95ff-957d8fdf91a2",
                roomState = TicTacToeGameRoom.State.WaitingForOpponent,
                opponentState = GameScreenState.OpponentState.WaitingForOpponent,
            ),
            isLoading = true,
        )

        yield(state)

        state = state.copy(
            room = state.room?.copy(
                roomState = TicTacToeGameRoom.State.InProgress,
                opponentState = GameScreenState.OpponentState.Known(
                    username = "User 2",
                    sign = TicTacToePlayerSign.O,
                    connected = true
                ),
                board = mapOf(
                    TicTacToeBoardCell(0, 0) to TicTacToePlayerSign.X,
                    TicTacToeBoardCell(0, 1) to TicTacToePlayerSign.O,
                ),
                isUserTurn = false,
            ),
            isLoading = false,
        )

        yield(state)

        yield(
            state.copy(
                dialog = AlertState(
                    title = "Error",
                    message = "you have been disconnected",
                    primaryAction = AlertState.Action(
                        label = "close",
                        onClick = {}
                    )
                ),
            )
        )

        state = state.copy(
            room = state.room?.copy(
                pastGamesWinners = listOf(
                    TicTacToePlayerSign.X,
                    null,
                    TicTacToePlayerSign.X,
                    TicTacToePlayerSign.O,
                    null,
                )
            )
        )

        yield(state)

        yieldAll(
            listOf(
                state.copy(
                    room = state.room?.copy(
                        finishState = GameScreenState.FinishState(
                            winnerSign = TicTacToePlayerSign.X,
                            userWon = true,
                            canRetry = false,
                        ),
                    )
                ),
                state.copy(
                    room = state.room?.copy(
                        finishState = GameScreenState.FinishState(
                            winnerSign = TicTacToePlayerSign.X,
                            userWon = true,
                            canRetry = false,
                        ),
                    )
                ),
                state.copy(
                    room = state.room?.copy(
                        finishState = GameScreenState.FinishState(
                            winnerSign = TicTacToePlayerSign.X,
                            userWon = true,
                            canRetry = false,
                        ),
                    )
                ),
                state.copy(
                    room = state.room?.copy(
                        finishState = GameScreenState.FinishState(
                            winnerSign = TicTacToePlayerSign.X,
                            userWon = true,
                            canRetry = false,
                        ),
                    )
                ),
                state.copy(
                    room = state.room?.copy(
                        isClosed = true
                    )
                ),
            )
        )
    }
}

@Preview
@Composable
private fun e(
    @PreviewParameter(GameScreenContentPreviewProvider::class)
    state: GameScreenState,
) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            GameScreenContent(
                state = state,
                onEvent = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}