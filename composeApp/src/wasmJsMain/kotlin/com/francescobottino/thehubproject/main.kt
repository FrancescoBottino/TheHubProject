package com.francescobottino.thehubproject

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.SecureStorage
import com.francescobottino.thehubproject.security.WasmJsSecureStorage
import kotlinx.browser.document
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin {
        modules(
            module {
                singleOf<SecureStorage>(::WasmJsSecureStorage)
            }
        )
    }

    ComposeViewport(document.body!!) {
        App()
    }
}