package com.francescobottino.thehubproject

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import com.francescobottino.thehubproject.screens.splash.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val deepLinkHandler = koinInject<DeepLinkHandler>()
    val pendingNavigation = remember(Unit) { deepLinkHandler.getStartupPendingNavigation() }

    AppTheme {
        Navigator(SplashScreen(pendingNavigation)) { navigator ->
            CurrentScreen()
        }
    }
}