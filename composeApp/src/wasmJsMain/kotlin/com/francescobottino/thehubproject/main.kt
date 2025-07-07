package com.francescobottino.thehubproject

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.francescobottino.thehubproject.client_features.core.security.SecureStorage
import com.francescobottino.thehubproject.client_features.core.usecase.CopyToClipboardUseCase
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import com.francescobottino.thehubproject.security.WasmJsSecureStorage
import com.francescobottino.thehubproject.usecase.WasmJsCopyToClipboardUseCase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Napier.base(DebugAntilog())

    val koin = initKoin {
        modules(
            module {
                singleOf<SecureStorage>(::WasmJsSecureStorage)
                singleOf<CopyToClipboardUseCase>(::WasmJsCopyToClipboardUseCase)
            }
        )
    }

    val startupDeepLink = window.location.pathname
    koin.get<DeepLinkHandler>().storeStartupDeeplink(startupDeepLink)

    ComposeViewport(document.body!!) {
        App()
    }
}