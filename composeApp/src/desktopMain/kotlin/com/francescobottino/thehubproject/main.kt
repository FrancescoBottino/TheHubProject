package com.francescobottino.thehubproject

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.francescobottino.thehubproject.client_shared.security.SecureStorage
import com.francescobottino.thehubproject.client_shared.usecase.CopyToClipboardUseCase
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.DesktopSecureStorage
import com.francescobottino.thehubproject.usecase.DesktopCopyToClipboardUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun main() {
    initKoin {
        modules(
            module {
                singleOf<SecureStorage>(::DesktopSecureStorage)
                singleOf<CopyToClipboardUseCase>(::DesktopCopyToClipboardUseCase)
            }
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "The Hub Project",
        ) {
            App()
        }
    }
}