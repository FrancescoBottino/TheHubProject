package com.francescobottino.thehubproject.games.tictactoe.screens.user_games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.compose.localDI

object UserGamesScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { UserGamesScreenModel(di, navigator) }
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
                    text = "No rooms found",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        items(state.rooms) { room ->
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = shape, clip = true)
                    .clip(shape)
                    .background(color = MaterialTheme.colorScheme.surface, shape = shape)
                    .clickable {
                        onEvent(UserGamesScreenEvent.OnRoomClicked(room))
                    }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            ) {
                Column {
                    Text("ID: ${room.id}")
                    Text("Last update: ${room.lastUpdate}")
                }
            }
        }
    }

    if(state.isLoading) {
        Dialog(
            onDismissRequest = {},
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .shadow(elevation = 12.dp)
                        .background(color = Color.White)
                        .padding(16.dp),
                ) {
                    CircularProgressIndicator()
                    Text("Loading...")
                }
            }
        }
    }

    if(state.error != null) {
        Dialog(
            onDismissRequest = {},
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    modifier = Modifier
                        .shadow(elevation = 12.dp)
                        .background(color = Color.White)
                        .padding(16.dp),
                ) {
                    Text(state.error)
                    Button(onClick = { onEvent(UserGamesScreenEvent.OnDialogClosed) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun UserGamesScreenContentPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            UserGamesScreenContent(
                state = UserGamesScreenState(
                    rooms = (0..10).map {
                        UserGamesScreenState.Room(
                            id = "123",
                            lastUpdate = Instant.parse("2023-01-01T00:00:00Z").toLocalDateTime(TimeZone.UTC),
                        )
                    },
                    isLoading = false,
                ),
                onEvent = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}