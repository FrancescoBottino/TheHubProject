package com.francescobottino.thehubproject.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_features.core.navigation.DeeplinkParser
import com.francescobottino.thehubproject.config.Config
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import kotlin.uuid.ExperimentalUuidApi

// todo:
// IOS, Desktop.

class DeepLinkHandler: KoinComponent {
    private val deepLinkParsers by lazy { getKoin().getAll<DeeplinkParser<*>>() }

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
        if(!url.startsWith(Config.feHttpUrl+"/")) {
            Napier.d(tag = "DeepLinkHandler") { "invalid url" }
            return null
        }
        val path = url.removePrefix(Config.feHttpUrl+"/")

        val parser = deepLinkParsers.singleOrNull { path.startsWith(it.basePath) }

        val destination = parser
            ?.getFromPath(path)
            ?.getDestination()

        Napier.d(tag = "DeepLinkHandler") { "Getting screen from deeplink: $url -> $destination" }

        return destination
    }
}