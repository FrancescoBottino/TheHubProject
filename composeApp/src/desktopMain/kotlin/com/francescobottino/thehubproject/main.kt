package com.francescobottino.thehubproject

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import com.francescobottino.thehubproject.security.DesktopSecureStorage
import com.francescobottino.thehubproject.security.SecureStorage
import org.kodein.di.DI
import org.kodein.di.bindSingleton

fun main() {
    DIProvider.di = DI {
        import(commonModule)
        bindSingleton<SecureStorage> { DesktopSecureStorage() }
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