package com.francescobottino.thehubproject

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.francescobottino.thehubproject.client_shared.security.SecureStorage
import com.francescobottino.thehubproject.client_shared.usecase.CopyToClipboardUseCase
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import com.francescobottino.thehubproject.security.WasmJsSecureStorage
import com.francescobottino.thehubproject.usecase.WasmJsCopyToClipboardUseCase
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
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