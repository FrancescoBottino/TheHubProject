package com.francescobottino.thehubproject

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import com.francescobottino.thehubproject.security.SecureStorage
import com.francescobottino.thehubproject.security.WasmJsSecureStorage
import kotlinx.browser.document
import org.kodein.di.DI
import org.kodein.di.bindSingleton

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    DIProvider.di = DI {
        import(commonModule)
        bindSingleton<SecureStorage> { WasmJsSecureStorage() }
    }

    ComposeViewport(document.body!!) {
        App()
    }
}