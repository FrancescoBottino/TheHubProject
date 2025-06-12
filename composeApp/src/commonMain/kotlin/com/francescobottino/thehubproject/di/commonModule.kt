package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.clientFeatureModule_Auth
import com.francescobottino.thehubproject.games.tictactoe.di.tictactoeModule
import com.francescobottino.thehubproject.network.TestApiService
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.network.makeHttpClient
import com.francescobottino.thehubproject.repo.AuthRepository
import io.ktor.client.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    single<HttpClient> { makeHttpClient { getKoin().get<AuthRepository>().getToken() } }
    singleOf(::UserApi)
    singleOf(::TestApiService)

    this.includes(
        sharedClientModule,
        clientFeatureModule_Auth,

        tictactoeModule,
    )
}