package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.clientFeatureModule_Auth
import com.francescobottino.thehubproject.games.tictactoe.di.tictactoeModule
import com.francescobottino.thehubproject.network.TestApiService
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.network.makeHttpClient
import com.francescobottino.thehubproject.repo.AuthRepository
import io.ktor.client.*
import org.kodein.di.*

val commonModule = DI.Module("commonModule") {
    bindSingleton<HttpClient> { makeHttpClient { di.direct.instance<AuthRepository>().getToken() } }
    bindSingleton<UserApi> { UserApi(instance()) }
    bindSingleton<TestApiService> { TestApiService(instance()) }

    import(sharedClientModule)
    import(clientFeatureModule_Auth)

    bindSet<GameModule>()

    importAll(
        tictactoeModule,

    )
}