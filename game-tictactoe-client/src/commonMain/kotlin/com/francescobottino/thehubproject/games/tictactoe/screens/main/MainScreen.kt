package com.francescobottino.thehubproject.games.tictactoe.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.compose.localDI

object MainScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MainScreenModel(di, navigator) }
        val state by screenModel.state.collectAsState()

        MainScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun MainScreenContent(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onEvent(MainScreenEvent.OnCreateRoom) }
        ) {
            Text("New Game")
        }

        HorizontalDivider()

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.searchedRoomId,
                onValueChange = { onEvent(MainScreenEvent.OnSearchedRoomIdChanged(it)) },
                modifier = Modifier.weight(1f),
            )

            Button(
                onClick = { onEvent(MainScreenEvent.OnJoinRoom) }
            ) {
                Text("Join Game")
            }
        }

        HorizontalDivider()

        Button(
            onClick = { onEvent(MainScreenEvent.OnSeeMyGames) }
        ) {
            Text("My Games")
        }

        Spacer(Modifier.height(24.dp))
    }

    if(state.dialogMessagesQueue.isNotEmpty()) {
        val dialogMessage = state.dialogMessagesQueue.first()

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
                    Text(dialogMessage)
                    Button(onClick = { onEvent(MainScreenEvent.OnDialogClosed) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenContentPreview() {
    MainScreenContent(
        state = MainScreenState(),
        onEvent = {},
    )
}