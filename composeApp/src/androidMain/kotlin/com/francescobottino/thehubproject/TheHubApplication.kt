package com.francescobottino.thehubproject

import android.app.Application
import android.content.Context
import com.francescobottino.thehubproject.auth.AndroidTokenStorage
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.di.commonModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class TheHubApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        DIProvider.di = DI {
            import(commonModule)
            bindSingleton<Context> { this@TheHubApplication.applicationContext }
            bindSingleton<TokenStorage> { AndroidTokenStorage(instance()) }
        }
    }
}