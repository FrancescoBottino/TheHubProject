package com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertCardOverlay
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertState
import com.francescobottino.thehubproject.client_features.core.ui.components.LoadingCardOverlay
import com.francescobottino.thehubproject.client_features.core.ui.images.Vs
import com.francescobottino.thehubproject.games.tictactoe.client.ui.components.PastWinnersRowCompact
import com.francescobottino.thehubproject.games.tictactoe.client.ui.components.SignIcon
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

object UserGamesScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { UserGamesScreenModel(navigator) }
        val state by screenModel.state.collectAsState()

        UserGamesScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun UserGamesScreenContent(
    state: UserGamesScreenState,
    onEvent: (UserGamesScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
    ) {
        item {
            Text(
                text = "Your rooms",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if(state.rooms.isEmpty()) {
            item {
                Text(
                    text = "You aren't in any GameRoom, start a new game or join an existing one",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            items(state.rooms) { room ->
                RoomCard(
                    room = room,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    LoadingCardOverlay(state.isLoading)

    val genericError = remember(state.error) {
        AlertState(
            title = "Error",
            message = state.error,
            primaryAction = AlertState.Action(
                label = "OK",
                onClick = { onEvent(UserGamesScreenEvent.OnDialogClosed) }
            ),
        )
    }

    AlertCardOverlay(
        if(state.error != null) {
            genericError
        } else {
            null
        }
    )
}

@Composable
private fun RoomCard(
    room: UserGamesScreenState.Room,
    onEvent: (UserGamesScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        modifier = modifier
            .shadow(elevation = 8.dp, shape = shape, clip = true)
            .background(color = MaterialTheme.colorScheme.surface, shape = shape)
            .clip(shape)
            .clickable { onEvent(UserGamesScreenEvent.OnRoomClicked(room)) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = room.id,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(start = 8.dp, top = 4.dp)
            )

            RoomStatusChip(
                room,
                modifier = Modifier.padding(end = 4.dp, top = 4.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            PlayerDisplay(
                playerName = "You",
                playerSign = room.userSign,
            )

            Icon(
                imageVector = Vs,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )

            PlayerDisplay(
                playerName = room.opponentName ?: "Waiting...",
                playerSign = room.userSign.otherSign(),
            )
        }

        if(room.winners.isNotEmpty()) {
            PastWinnersRowCompact(
                winners = room.winners,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 12.dp)
            )
        }

        Text(
            text = room.lastUpdate,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RoomStatusChip(
    room: UserGamesScreenState.Room,
    modifier: Modifier = Modifier
) {
    Text(
        text = room.state.label,
        style = MaterialTheme.typography.labelSmall,
        color = room.state.color,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .background(color = room.state.color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun PlayerDisplay(
    playerName: String,
    playerSign: TicTacToePlayerSign,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredSize(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            SignIcon(
                sign = playerSign,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            text = playerName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private class UserGamesScreenStatePreview: PreviewParameterProvider<UserGamesScreenState> {
    override val values: Sequence<UserGamesScreenState>
        get() {
            var room = UserGamesScreenState.Room(
                id = "2f9f6008-8b10-4d56-95ff-957d8fdf91a2",
                lastUpdate = "5 mins ago",
                state = UserGamesScreenState.Room.State.Waiting,
                opponentName = null,
                winners = emptyMap(),
                userIsHost = true,
                userSign = TicTacToePlayerSign.X,
                userTurn = true,
            )

            val rooms = buildList {
                add(room)

                room = room.copy(
                    state = UserGamesScreenState.Room.State.Playing("Your turn"),
                    opponentName = "User 2",
                )
                
                add(room)

                room = room.copy(
                    state = UserGamesScreenState.Room.State.Finished("Wait your turn"),
                )

                add(room)

                room = room.copy(
                    state = UserGamesScreenState.Room.State.Finished("You won"),
                )

                add(room)

                room = room.copy(
                    state = UserGamesScreenState.Room.State.Finished("You lost"),
                    winners = room.winners + (TicTacToePlayerSign.X to (room.winners[TicTacToePlayerSign.X] ?: 0) + 1),
                )

                add(room)

                room = room.copy(
                    state = UserGamesScreenState.Room.State.Finished("Tied"),
                    winners = room.winners + (TicTacToePlayerSign.O to (room.winners[TicTacToePlayerSign.O] ?: 0) + 1),
                )

                add(room)

                room = room.copy(
                    state = UserGamesScreenState.Room.State.Closed,
                    winners = room.winners + (null to (room.winners[null] ?: 0) + 1),
                )

                add(room)
            }

            return UserGamesScreenState(
                rooms = rooms,
                isLoading = false,
            ).let {
                sequenceOf(
                    it.copy(isLoading = false, error = null),
                    it.copy(isLoading = true, error = null),
                    it.copy(isLoading = false, error = "Error"),
                )
            }
        }
}

@Preview
@Composable
private fun UserGamesScreenContentPreview(
    @PreviewParameter(UserGamesScreenStatePreview::class)
    state: UserGamesScreenState
) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            UserGamesScreenContent(
                state = state,
                onEvent = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}