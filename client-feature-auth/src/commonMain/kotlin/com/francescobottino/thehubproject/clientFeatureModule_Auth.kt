package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.repo.AuthRepository
import com.francescobottino.thehubproject.repo.AuthRepositoryImplementation
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val clientFeatureModule_Auth = DI.Module("clientFeatureModule_Auth") {
    bindSingleton<AuthRepository> { AuthRepositoryImplementation(instance(), instance()) }
}