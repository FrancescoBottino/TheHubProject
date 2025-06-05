package com.francescobottino.thehubproject

import androidx.compose.ui.window.ComposeUIViewController
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import com.francescobottino.thehubproject.security.IOSSecureStorage
import com.francescobottino.thehubproject.security.SecureStorage
import org.kodein.di.DI
import org.kodein.di.bindSingleton

@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    DIProvider.di = DI {
        import(commonModule)
        bindSingleton<SecureStorage> { IOSSecureStorage() }
    }

    App()
}