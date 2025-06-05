package com.francescobottino.thehubproject.screens.game_selection

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.screens.login.LoginScreen
import kotlinx.coroutines.launch
import org.kodein.di.compose.localDI
import org.kodein.di.instance

object GameSelectionScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val tokenStorage by di.instance<TokenStorage>()
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        val games: Set<GameModule> by di.instance()

        Column {
            Button({
                scope.launch {
                    tokenStorage.clearToken()
                    navigator.replace(LoginScreen)
                }
            }) { Text("Logout") }

            games.forEach { game ->
                Button({
                    navigator.push(game.getMainScreen())
                }) { Text(game.getName()) }
            }
        }
    }
}