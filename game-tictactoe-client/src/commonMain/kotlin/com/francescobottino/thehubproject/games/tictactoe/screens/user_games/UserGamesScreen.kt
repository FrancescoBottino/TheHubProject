package com.francescobottino.thehubproject.games.tictactoe.screens.user_games

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.screens.manageEvents
import org.kodein.di.compose.localDI

object UserGamesScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { UserGamesScreenModel(di) }
        val state by screenModel.state.collectAsState()

        UserGamesScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )

        screenModel.manageEvents {
            TODO()
        }
    }
}

@Composable
private fun UserGamesScreenContent(
    state: UserGamesScreenState = UserGamesScreenState(),
    onEvent: (UserGamesScreenEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    TODO()
}