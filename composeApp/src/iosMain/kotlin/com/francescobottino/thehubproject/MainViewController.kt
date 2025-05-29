package com.francescobottino.thehubproject

import androidx.compose.ui.window.ComposeUIViewController
import com.francescobottino.thehubproject.auth.IOSTokenStorage
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton

fun MainViewController() = ComposeUIViewController {
    DIProvider.di = DI {
        import(commonModule)
        bindSingleton<TokenStorage> { IOSTokenStorage() }
    }

    App()
}