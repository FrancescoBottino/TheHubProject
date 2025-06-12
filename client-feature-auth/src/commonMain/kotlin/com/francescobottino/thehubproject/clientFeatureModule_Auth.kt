package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.repo.AuthRepository
import com.francescobottino.thehubproject.repo.AuthRepositoryImplementation
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val clientFeatureModule_Auth = module {
    singleOf(::AuthRepositoryImplementation) bind AuthRepository::class
}