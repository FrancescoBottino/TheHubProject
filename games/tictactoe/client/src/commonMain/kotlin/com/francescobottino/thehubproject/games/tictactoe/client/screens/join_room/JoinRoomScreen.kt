package com.francescobottino.thehubproject.games.tictactoe.client.screens.join_room

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertCardOverlay
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertState
import com.francescobottino.thehubproject.client_features.core.ui.components.LoadingCardOverlay

class JoinRoomScreen(val roomId: String): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel by remember { mutableStateOf(JoinRoomScreenModel(navigator, roomId)) }

        val state by screenModel.state.collectAsState()

        JoinRoomScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun JoinRoomScreenContent(
    state: JoinRoomScreenState,
    onEvent: (JoinRoomScreenEvent) -> Unit,
    modifier: Modifier= Modifier,
) {
    if(state.error != null) {
        AlertCardOverlay(
            state = AlertState(
                title = "Error",
                message = state.error,
                dismissable = false,
                primaryAction = AlertState.Action(
                    label = "Retry",
                    onClick = { onEvent(JoinRoomScreenEvent.Retry) }
                )
            )
        )
    } else {
        LoadingCardOverlay(
            text = "Attempting to join game room...",
            modifier = modifier,
        )
    }
}