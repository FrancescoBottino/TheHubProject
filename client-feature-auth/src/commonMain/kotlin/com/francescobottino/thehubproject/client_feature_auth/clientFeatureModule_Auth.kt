package com.francescobottino.thehubproject.client_feature_auth

import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val clientFeatureModule_Auth = module {
    singleOf(::AuthRepositoryImplementation) bind AuthRepository::class
}