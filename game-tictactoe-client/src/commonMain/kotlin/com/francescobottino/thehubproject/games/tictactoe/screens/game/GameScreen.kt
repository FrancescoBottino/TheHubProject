package com.francescobottino.thehubproject.games.tictactoe.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.games.tictactoe.presentation.TicTacToeCircle
import com.francescobottino.thehubproject.games.tictactoe.presentation.TicTacToeCross
import org.kodein.di.compose.localDI

class GameScreen(private val roomId: String): Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val screenModel by remember { mutableStateOf(GameScreenModel(di, roomId)) }
        val navigator = LocalNavigator.currentOrThrow

        val state by screenModel.state.collectAsState()

        GameScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )

        if(state.userConnection is GameScreenState.ConnectionState.Connecting || state.isLoading) {
            //todo loading
        }

        /*
        OnGameFinishedDialog(
            state = state,
            onEvent = screenModel::onEvent,
        )

         */

        DisconnectionDialog(
            state = state,
            onDisconnectedConfirm = { navigator.pop() }
        )

        LaunchedEffect(Unit) {
            screenModel.connectToRoom()
        }
    }
}

@Composable
private fun GameScreenContent(
    state: GameScreenState,
    onEvent: (GameScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OpponentConnectionStatus(
                connected = state.opponentConnected,
                label = state.opponentLabel,
                modifier = Modifier.weight(1f),
            )
        }

        Board(
            state = state.board,
            isUserTurn = state.isUserTurn,
            onMove = { cell -> onEvent(GameScreenEvent.OnUserClickedCell(cell))}
        )

        /*
        Button(
            onClick = { onEvent(GameScreenEvent.OnCloseRoom)) },
            modifier = Modifier.fillMaxWidth(),
        )

         */
    }
}

@Composable
private fun OpponentConnectionStatus(
    connected: Boolean,
    label: String?,
    modifier: Modifier = Modifier,
) {
    val color = if(connected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.error

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
                .padding(6.dp),
        ) {
            Text(text = label ?: "")
            Box(modifier = Modifier.requiredSize(16.dp).clip(CircleShape).background(color))
        }
    }
}

@Composable
private fun Board(
    state: TicTacToeGameState,
    isUserTurn: Boolean,
    onMove: (TicTacToeBoardCell) -> Unit,
) {
    Column(
        modifier = Modifier.requiredSize(400.dp),
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
                            val signDrawable = when (it) {
                                TicTacToePlayerSign.O -> TicTacToeCircle
                                TicTacToePlayerSign.X -> TicTacToeCross
                            }
                            Icon(
                                imageVector = signDrawable,
                                contentDescription = null,
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

@Composable
private fun DisconnectionDialog(
    state: GameScreenState,
    onDisconnectedConfirm: () -> Unit,
) {
    if(state.userConnection is GameScreenState.ConnectionState.Disconnected) {
        Dialog(
            onDismissRequest = {},
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
                        text = "Connection Lost",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Text(
                        text = "You are disconnected from the room. You will be redirected to the main screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )

                    state.userConnection.reason?.let {
                        Text(
                            text = "Reason: ${it.message ?: it::class.simpleName}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }

                    Button(
                        onClick = onDisconnectedConfirm,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}