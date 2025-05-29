package com.francescobottino.thehubproject

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.auth.WasmJsTokenStorage
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import kotlinx.browser.document
import org.kodein.di.DI
import org.kodein.di.bindSingleton

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    DIProvider.di = DI {
        import(commonModule)
        bindSingleton<TokenStorage> { WasmJsTokenStorage() }
    }

    ComposeViewport(document.body!!) {
        App()
    }
}