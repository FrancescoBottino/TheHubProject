package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.client_feature_auth.clientFeatureModule_Auth
import com.francescobottino.thehubproject.client_shared.di.WEBSOCKET_ENDPOINT
import com.francescobottino.thehubproject.client_shared.di.sharedClientModule
import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.config.Config
import com.francescobottino.thehubproject.games.tictactoe.client.di.tictactoeModule
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.network.makeHttpClient
import io.ktor.client.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single<HttpClient> { makeHttpClient { getKoin().get<AuthRepository>().getToken() } }
    single<String>(named(WEBSOCKET_ENDPOINT)) { Config.wsUrl }
    singleOf(::UserApi)
    singleOf(::DeepLinkHandler)

    this.includes(
        sharedClientModule,
        clientFeatureModule_Auth,

        tictactoeModule,
    )
}