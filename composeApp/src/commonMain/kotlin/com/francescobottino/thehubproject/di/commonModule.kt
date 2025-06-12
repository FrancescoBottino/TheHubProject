package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.clientFeatureModule_Auth
import com.francescobottino.thehubproject.config.Config
import com.francescobottino.thehubproject.games.tictactoe.di.tictactoeModule
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.network.makeHttpClient
import com.francescobottino.thehubproject.repo.AuthRepository
import io.ktor.client.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single<HttpClient> { makeHttpClient { getKoin().get<AuthRepository>().getToken() } }
    single<String>(named(WEBSOCKET_ENDPOINT)) { Config.wsUrl }
    singleOf(::UserApi)

    this.includes(
        sharedClientModule,
        clientFeatureModule_Auth,

        tictactoeModule,
    )
}