package com.francescobottino.thehubproject.games.tictactoe.client.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.components.LoadingCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.components.SimpleError
import com.francescobottino.thehubproject.client_shared.ui.components.SimpleErrorCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

object MainScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MainScreenModel(navigator) }
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
    Box(
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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

        LoadingCardOverlay(state.isLoading)

        val genericError = remember(state.error) {
            SimpleError(
                title = "Error",
                message = state.error,
                action = "OK" to { onEvent(MainScreenEvent.OnDialogClosed) },
            )
        }

        SimpleErrorCardOverlay(
            if(state.error != null) {
                genericError
            } else {
                null
            }
        )
    }
}

private class MainScreenStatePreview: PreviewParameterProvider<MainScreenState> {
    override val values: Sequence<MainScreenState>
        get() = sequenceOf(
            MainScreenState(isLoading = false, error = null),
            MainScreenState(isLoading = true, error = null),
            MainScreenState(isLoading = false, error = "Error"),
        )
}

@Preview
@Composable
private fun MainScreenContentPreview(
    @PreviewParameter(MainScreenStatePreview::class)
    state: MainScreenState
) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreenContent(
                state = state,
                onEvent = {},
            )
        }
    }
}