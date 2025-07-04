package com.francescobottino.thehubproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import com.francescobottino.thehubproject.screens.splash.SplashScreen
import kotlinx.coroutines.flow.*

// todo:
// IOS, Desktop.

class DeepLinkHandler {
    private var startupPendingNavigation: Screen? = null
    fun getStartupPendingNavigation() = startupPendingNavigation?.also { startupPendingNavigation = null }
    fun storeStartupDeeplink(deepLink: String) {
        getScreenFromDeepLink(deepLink)?.let {
            startupPendingNavigation = it
        }
    }

    private val _onDeepLinkReceivedFlow = MutableSharedFlow<String>()
    val onDeepLinkReceivedFlow: SharedFlow<String> = _onDeepLinkReceivedFlow.asSharedFlow()
    fun onDeepLinkReceived(url: String) {
        _onDeepLinkReceivedFlow.tryEmit(url)
    }

    @Composable
    fun handleDeepLinks(navigator: Navigator) {
        LaunchedEffect(Unit) {
            onDeepLinkReceivedFlow
                .onEach { deepLink -> handleNavigation(navigator, deepLink) }
                .launchIn(this)
        }
    }

    private fun handleNavigation(navigator: Navigator, deepLink: String) {
        getScreenFromDeepLink(deepLink)?.let {
            navigator.push(SplashScreen(pendingNavigation = it)) //todo use proper screen to skeep splash / login if already logged
        }
    }

    private fun getScreenFromDeepLink(url: String): Screen? {
        return TODO()
    }
}