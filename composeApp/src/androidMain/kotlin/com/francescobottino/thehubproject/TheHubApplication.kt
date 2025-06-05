package com.francescobottino.thehubproject

import android.app.Application
import android.content.Context
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import com.francescobottino.thehubproject.security.AndroidSecureStorage
import com.francescobottino.thehubproject.security.SecureStorage
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class TheHubApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        DIProvider.di = DI {
            bindSingleton<Context> { this@TheHubApplication.applicationContext }
            bindSingleton<SecureStorage> { AndroidSecureStorage(instance()) }

            import(commonModule)
        }
    }
}