package com.francescobottino.thehubproject

import android.app.Application
import com.francescobottino.thehubproject.client_features.core.security.SecureStorage
import com.francescobottino.thehubproject.client_features.core.usecase.CopyToClipboardUseCase
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.AndroidSecureStorage
import com.francescobottino.thehubproject.usecase.AndroidCopyToClipboardUseCase
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class TheHubApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        initKoin {
            androidLogger()
            androidContext(this@TheHubApplication)

            modules(
                module {
                    single<SecureStorage> { AndroidSecureStorage(get()) }
                    single<CopyToClipboardUseCase> { AndroidCopyToClipboardUseCase(get()) }
                }
            )
        }
    }
}