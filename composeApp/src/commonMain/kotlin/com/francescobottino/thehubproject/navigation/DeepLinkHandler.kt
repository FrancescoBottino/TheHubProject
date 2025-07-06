package com.francescobottino.thehubproject.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.config.Config
import com.francescobottino.thehubproject.games.tictactoe.client.screens.game.GameScreen
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.uuid.ExperimentalUuidApi

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

    @OptIn(ExperimentalUuidApi::class)
    private fun getScreenFromDeepLink(url: String): Screen? {
        if(!url.startsWith(Config.apiHttpUrl+"/")) {
            Napier.d(tag = "DeepLinkHandler") { "invalid url" }
            return null
        }
        val path = url.removePrefix(Config.apiHttpUrl+"/")

        val screen = when {
            path.startsWith("tictactoe/invite/") -> {
                val roomId = path.removePrefix("tictactoe/invite/")
                Napier.d(tag = "DeepLinkHandler") { "tictactoe invite for id $roomId" }
                GameScreen(roomId = roomId)
            }

            else -> null
        }

        Napier.d(tag = "DeepLinkHandler") { "Getting screen from deeplink: $url -> $screen" }
        return screen
    }
}