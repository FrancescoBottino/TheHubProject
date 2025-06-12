package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.di.initKoin
import com.francescobottino.thehubproject.security.IOSSecureStorage
import com.francescobottino.thehubproject.security.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun initKoinForIOS() {
    initKoin {
        modules(
            module {
                singleOf<SecureStorage>(::IOSSecureStorage)
            }
        )
    }
}