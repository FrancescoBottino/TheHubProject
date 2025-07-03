package com.francescobottino.thehubproject

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import com.francescobottino.thehubproject.config.Config
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import com.francescobottino.thehubproject.screens.splash.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val deepLinkHandler = koinInject<DeepLinkHandler>()

    AppTheme {
        Navigator(SplashScreen) { navigator ->
            deepLinkHandler.handleDeepLinks(navigator)
            Column {
                Text("env: "+ ClientConfig.ENVIRONMENT)
                Text("deeplinkDomain: "+ ClientConfig.DEEP_LINK_DOMAIN)
                Text("httpUrl: "+ Config.httpUrl)
                CurrentScreen()
            }
        }
    }
}