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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.components.LoadingCardOverlay
import com.francescobottino.thehubproject.games.tictactoe.client.presentation.TicTacToeCatGame
import com.francescobottino.thehubproject.games.tictactoe.client.presentation.TicTacToeCircle
import com.francescobottino.thehubproject.games.tictactoe.client.presentation.TicTacToeCross
import com.francescobottino.thehubproject.games.tictactoe.client.presentation.TicTacToeCrown
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import compose.icons.FeatherIcons
import compose.icons.feathericons.Copy
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    if(state.roomId != null) {
        Column(
            modifier = modifier
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            OpponentConnectionStatus(
                opponentState = state.opponentState,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(30.dp))

            TurnIndicator(state = state)

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
                    state = state.board,
                    isUserTurn = state.isUserTurn,
                    onMove = { cell -> onEvent(GameScreenEvent.OnBoardCellClicked(cell))},
                    modifier = Modifier.fillMaxSize()
                )

                state.finishState?.let {
                    FinishDialog(
                        finishState = it,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }


            Spacer(Modifier.height(40.dp))

            RoomIdCard(
                roomId = state.roomId,
                onClick = { onEvent(GameScreenEvent.OnCopyRoomId) }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onEvent(GameScreenEvent.OnCloseRoom) },
                enabled = false, //todo
            ) {
                Text("Close room")
            }

            Spacer(Modifier.height(48.dp))

            /*
            past winners
             */

            /*
            when(state.roomState) {
                is TicTacToeGameRoom.State.WaitingForOpponent -> TODO()
                is TicTacToeGameRoom.State.InProgress -> TODO()
                is TicTacToeGameRoom.State.Finished -> TODO()
                is TicTacToeGameRoom.State.Closed -> TODO()
            }

             */
        }
    }

    if(state.roomId == null || state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingCardOverlay()
        }
    }

    state.dialog?.let {
        ScreenDialog(it, onEvent)
    }
}

@Composable
private fun OpponentConnectionStatus(
    opponentState: GameScreenState.OpponentState,
    modifier: Modifier = Modifier,
) {
    val color = when(opponentState) {
        is GameScreenState.OpponentState.WaitingForOpponent -> MaterialTheme.colorScheme.onSurface
        is GameScreenState.OpponentState.Connected -> MaterialTheme.colorScheme.primary
        is GameScreenState.OpponentState.Disconnected -> MaterialTheme.colorScheme.error
    }

    val label = when(opponentState) {
        is GameScreenState.OpponentState.WaitingForOpponent -> "Waiting for opponent..."
        is GameScreenState.OpponentState.Connected -> opponentState.username
        is GameScreenState.OpponentState.Disconnected -> opponentState.username ?: "Disconnected"
    }

    val shape = RoundedCornerShape(50)

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .shadow(elevation = 12.dp, shape = shape, clip = true)
                .clip(shape)
                .border(width = 1.dp, color = color, shape = shape)
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(vertical = 6.dp, horizontal = 12.dp),
        ) {
            Text(text = label)
            if(opponentState is GameScreenState.OpponentState.WaitingForOpponent) {
                Box(modifier = Modifier.requiredSize(16.dp).clip(CircleShape).background(color)) {
                    CircularProgressIndicator()
                }
            } else {
                Box(modifier = Modifier.requiredSize(16.dp).clip(CircleShape).background(color))
            }
        }
    }
}

@Composable
private fun TurnIndicator(
    state: GameScreenState,
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
                    finishState.winnerSign?.let {
                        SignIcon(
                            sign = it,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } ?: run {
                        Icon(
                            TicTacToeCatGame,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
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
private fun SignIcon(
    sign: TicTacToePlayerSign,
    modifier: Modifier = Modifier,
) {
    val signDrawable = when (sign) {
        TicTacToePlayerSign.O -> TicTacToeCircle
        TicTacToePlayerSign.X -> TicTacToeCross
    }
    val color = when (sign) {
        TicTacToePlayerSign.O -> Color(0xFFFF5C00)
        TicTacToePlayerSign.X -> Color(0xFF305CDE)
    }
    Icon(
        imageVector = signDrawable,
        contentDescription = null,
        tint = color,
        modifier = modifier,
    )
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
private fun ScreenDialog(
    state: GameScreenState.Dialog,
    onEvent: (GameScreenEvent) -> Unit,
) {
    Dialog(
        onDismissRequest = {
            if(state.dismissable) {
                onEvent(GameScreenEvent.OnDismissDialog)
            }
        },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 32.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .shadow(elevation = 12.dp)
                    .background(color = Color.White)
                    .padding(16.dp),
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                if(state.message != null) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                if(state.onConfirm != null || state.onDismiss != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        if(state.onDismiss != null) {
                            TextButton(
                                onClick = { onEvent(state.onDismiss.event) },
                            ) {
                                Text(state.onDismiss.label)
                            }
                        }
                        if(state.onConfirm != null) {
                            Button(
                                onClick = { onEvent(state.onConfirm.event) },
                            ) {
                                Text(state.onConfirm.label)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GameScreenContentPreview_WaitingForOpponent() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            GameScreenContent(
                state = GameScreenState(
                    roomId = "2f9f6008-8b10-4d56-95ff-957d8fdf91a2",
                    roomState = TicTacToeGameRoom.State.WaitingForOpponent,
                    opponentState = GameScreenState.OpponentState.WaitingForOpponent,
                ),
                onEvent = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun GameScreenContentPreview_InProgress() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            GameScreenContent(
                state = GameScreenState(
                    roomId = "2f9f6008-8b10-4d56-95ff-957d8fdf91a2",
                    roomState = TicTacToeGameRoom.State.InProgress,
                    opponentState = GameScreenState.OpponentState.Connected("User 2"),
                    board = mapOf(
                        TicTacToeBoardCell(0, 0) to TicTacToePlayerSign.X,
                        TicTacToeBoardCell(0, 1) to TicTacToePlayerSign.O,
                    ),
                    isUserTurn = true,
                ),
                onEvent = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun GameScreenContentPreview_Finished() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            GameScreenContent(
                state = GameScreenState(
                    roomId = "2f9f6008-8b10-4d56-95ff-957d8fdf91a2",
                    roomState = TicTacToeGameRoom.State.InProgress,
                    opponentState = GameScreenState.OpponentState.Disconnected("User 2"),
                    board = mapOf(
                        TicTacToeBoardCell(0, 0) to TicTacToePlayerSign.X,
                        TicTacToeBoardCell(0, 1) to TicTacToePlayerSign.O,
                    ),
                    isUserTurn = true,
                    finishState = GameScreenState.FinishState(
                        winnerSign = TicTacToePlayerSign.X,
                        userWon = true,
                        canRetry = false,
                    )
                ),
                onEvent = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}