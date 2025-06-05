package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.network.ApiService
import com.francescobottino.thehubproject.network.makeHttpClient
import com.francescobottino.thehubproject.repo.InMemoryUserRepository
import com.francescobottino.thehubproject.repo.UserRepository
import io.ktor.client.*
import org.kodein.di.DI
import org.kodein.di.bindSet
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val commonModule = DI.Module("commonModule") {
    bindSingleton<TokenStorage> { TokenStorage(instance()) }
    bindSingleton<HttpClient> { makeHttpClient(instance()) }
    bindSingleton<ApiService> { ApiService(instance(), instance()) }
    bindSingleton<UserRepository> { InMemoryUserRepository() }

    bindSet<GameModule>()

    /*
    importAll(
        tictactoeModule,

    )

     */
}