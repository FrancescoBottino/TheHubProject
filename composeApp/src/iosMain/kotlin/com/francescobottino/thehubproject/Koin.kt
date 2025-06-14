package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.client_shared.security.SecureStorage
import com.francescobottino.thehubproject.client_shared.usecase.CopyToClipboardUseCase
import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.IosSecureStorage
import com.francescobottino.thehubproject.usecase.IosCopyToClipboardUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun initKoinForIOS() {
    initKoin {
        modules(
            module {
                singleOf<SecureStorage>(::IosSecureStorage)
                singleOf<CopyToClipboardUseCase>(::IosCopyToClipboardUseCase)
            }
        )
    }
}