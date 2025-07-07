package com.francescobottino.thehubproject.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.config.Config
import com.francescobottino.thehubproject.games.tictactoe.client.screens.user_games.UserGamesScreen
import io.github.aakira.napier.Napier
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

// todo:
// IOS, Desktop.

class DeepLinkHandler {
    private var startupPendingNavigation: Screen? = null
    fun getStartupPendingNavigation() = startupPendingNavigation?.also { startupPendingNavigation = null }
    fun storeStartupDeeplink(deepLink: String) {
        Napier.d(tag = "DeepLinkHandler") { "Starting deep link: $deepLink" }
        getScreenFromDeepLink(deepLink)?.let {
            startupPendingNavigation = it
        }
    }

    private val _onDeepLinkReceivedFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    val onDeepLinkReceivedFlow: SharedFlow<String> = _onDeepLinkReceivedFlow.asSharedFlow()
    fun tryOnDeepLinkReceived(url: String) {
        Napier.d(tag = "DeepLinkHandler") { "Deep link received: $url" }
        _onDeepLinkReceivedFlow.tryEmit(url)
    }

    suspend fun onDeepLinkReceived(url: String) {
        Napier.d(tag = "DeepLinkHandler") { "Deep link received: $url" }
        _onDeepLinkReceivedFlow.emit(url)
    }

    fun handleDeepLinks(scope: CoroutineScope, navigator: Navigator) {
        onDeepLinkReceivedFlow
            .onEach { deepLink ->
                Napier.d(tag = "DeepLinkHandler") { "Deep link collected: $deepLink" }
                handleNavigation(navigator, deepLink) }
            .launchIn(scope)
    }

    private fun handleNavigation(navigator: Navigator, deepLink: String) {
        getScreenFromDeepLink(deepLink)?.let {
            Napier.d(tag = "DeepLinkHandler") { "Deeplink handler: navigating to screen $it" }
            navigator.push(it)
        }
    }

    private fun getScreenFromDeepLink(url: String): Screen? {
        if(!url.startsWith(Config.httpUrl)) return null
        Napier.d(tag = "DeepLinkHandler") { "segments : " + Url(url).rawSegments.toString() }
        val screen = UserGamesScreen //todo calculate actual screen
        Napier.d(tag = "DeepLinkHandler") { "Getting screen from deeplink: $url -> $screen" }
        return screen
    }
}