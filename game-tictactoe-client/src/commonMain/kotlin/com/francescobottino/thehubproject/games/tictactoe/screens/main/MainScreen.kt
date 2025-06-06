package com.francescobottino.thehubproject.games.tictactoe.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.games.tictactoe.screens.game.GameScreen
import com.francescobottino.thehubproject.screens.manageEvents
import org.kodein.di.compose.localDI

object MainScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MainScreenModel(di) }
        val state by screenModel.state.collectAsState()

        MainScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )

        screenModel.manageEvents { event ->
            when(event) {
                is MainScreenModelEvent.NavigateToGameScreen -> navigator.push(GameScreen(event.roomId))
            }
        }
    }
}

@Composable
private fun MainScreenContent(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.searchedRoomId,
            onValueChange = { onEvent(MainScreenEvent.OnSearchedRoomIdChanged(it)) }
        )

        Button(
            onClick = { onEvent(MainScreenEvent.OnJoinRoom) }
        ) {
            Text("Join Game")
        }

        HorizontalDivider()

        Button(
            onClick = { onEvent(MainScreenEvent.OnCreateRoom) }
        ) {
            Text("New Game")
        }
    }
}