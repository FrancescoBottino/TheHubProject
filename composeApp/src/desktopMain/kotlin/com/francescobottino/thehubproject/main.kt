package com.francescobottino.thehubproject

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.DesktopSecureStorage
import com.francescobottino.thehubproject.security.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun main() {
    initKoin {
        modules(
            module {
                singleOf<SecureStorage>(::DesktopSecureStorage)
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