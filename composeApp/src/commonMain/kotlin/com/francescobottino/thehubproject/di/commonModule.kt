package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.network.ApiService
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val commonModule = DI.Module("commonModule") {
    bindSingleton { TokenStorage(instance()) }
    bindSingleton { ApiService(instance()) }
}