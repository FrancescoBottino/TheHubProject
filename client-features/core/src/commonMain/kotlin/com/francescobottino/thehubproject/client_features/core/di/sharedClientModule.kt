package com.francescobottino.thehubproject.client_features.core.di

import com.francescobottino.thehubproject.client_features.core.repo.InMemoryUserRepository
import com.francescobottino.thehubproject.client_features.core.repo.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedClientModule = module {
    singleOf(::InMemoryUserRepository) bind UserRepository::class
}