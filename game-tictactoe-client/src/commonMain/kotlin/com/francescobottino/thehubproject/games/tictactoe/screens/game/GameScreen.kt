package com.francescobottino.thehubproject.games.tictactoe.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.games.tictactoe.api.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import thehubproject.game_tictactoe_client.generated.resources.Res
import thehubproject.game_tictactoe_client.generated.resources.tic_tac_toe_circle
import thehubproject.game_tictactoe_client.generated.resources.tic_tac_toe_cross

class GameScreen(private val roomId: String): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val api by localDI().instance<TicTacToeApi>()

        var connected by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf("") }
        var state by remember { mutableStateOf<TicTacToeGameRoom?>(null) }

        LaunchedEffect(Unit) {
            api.joinRoomWebSocket(roomId)
                .distinctUntilChanged()
                .onEach { state = it }
                .onStart { connected = true }
                .onCompletion { connected = false }
                .launchIn(this)
        }

        Column {
            Button(onClick = { navigator.pop() }) { Text("Back") }
            Text(roomId)
            Text(error)
            Box(modifier = Modifier.requiredSize(40.dp).clip(CircleShape).background(if(connected) Color.Green else Color.Red))

            state?.let {
                Board(
                    state = it,
                    onMove = { cell ->
                        scope.launch {
                            runCatching {
                                api.makeMove(roomId, TicTacToeMakeMoveRequest(cell))
                            }
                        }
                    }
                )
            } ?: run {
                Text("Loading...")
            }
        }
    }
}

@Composable
private fun Board(
    state: TicTacToeGameRoom,
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

                    val sign = state.gameState[cell]

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = sign == null && state.roomState is TicTacToeGameRoom.State.InProgress) { onMove(cell) },
                        contentAlignment = Alignment.Center,
                    ) {
                        sign?.let {
                            val signDrawable = when (it) {
                                TicTacToePlayerSign.O -> Res.drawable.tic_tac_toe_circle
                                TicTacToePlayerSign.X -> Res.drawable.tic_tac_toe_cross
                            }
                            Icon(
                                painter = painterResource(signDrawable),
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