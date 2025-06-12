package com.francescobottino.thehubproject

import android.app.Application
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.AndroidSecureStorage
import com.francescobottino.thehubproject.security.SecureStorage
import com.francescobottino.thehubproject.usecase.AndroidCopyToClipboardUseCase
import com.francescobottino.thehubproject.usecase.CopyToClipboardUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class TheHubApplication: Application() {
    override fun onCreate() {
        super.onCreate()

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