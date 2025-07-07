package com.francescobottino.thehubproject.client_features.auth

import com.francescobottino.thehubproject.client_features.core.repo.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val clientFeatureModule_Auth = module {
    singleOf(::AuthRepositoryImplementation) bind AuthRepository::class
}