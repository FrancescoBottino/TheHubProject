package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.auth.AuthApi
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.games.tictactoe.di.tictactoeModule
import com.francescobottino.thehubproject.network.TestApiService
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.network.makeHttpClient
import io.ktor.client.*
import org.kodein.di.DI
import org.kodein.di.bindSet
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val commonModule = DI.Module("commonModule") {
    bindSingleton<TokenStorage> { TokenStorage(instance()) }
    bindSingleton<HttpClient> { makeHttpClient(instance()) }
    bindSingleton<AuthApi> { AuthApi(instance()) }
    bindSingleton<UserApi> { UserApi(instance()) }
    bindSingleton<TestApiService> { TestApiService(instance()) }

    import(sharedClientModule)

    bindSet<GameModule>()

    importAll(
        tictactoeModule,

    )
}