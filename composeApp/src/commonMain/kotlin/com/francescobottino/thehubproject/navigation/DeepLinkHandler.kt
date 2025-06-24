package com.francescobottino.thehubproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import kotlinx.coroutines.flow.*

// todo:
// IOS, Desktop, Wasm.
// Assetlinks.json and ios counterpart

class DeepLinkHandler {
    private val _onDeepLinkReceivedFlow = MutableSharedFlow<String>()
    val onDeepLinkReceivedFlow: SharedFlow<String> = _onDeepLinkReceivedFlow.asSharedFlow()

    fun onDeepLinkReceived(url: String) {
        _onDeepLinkReceivedFlow.tryEmit(url)
    }

    fun handleNavigation(navigator: Navigator, deepLink: String) {
        //todo proper handling
        navigator.push(MainHostScreen)
    }

    @Composable
    fun handleDeepLinks(navigator: Navigator) {
        LaunchedEffect(Unit) {
            onDeepLinkReceivedFlow
                .onEach { deepLink -> handleNavigation(navigator, deepLink) }
                .launchIn(this)
        }
    }
}