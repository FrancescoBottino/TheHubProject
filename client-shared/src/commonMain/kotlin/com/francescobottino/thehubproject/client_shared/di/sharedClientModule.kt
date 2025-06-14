package com.francescobottino.thehubproject.client_shared.di

import com.francescobottino.thehubproject.client_shared.repo.InMemoryUserRepository
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedClientModule = module {
    singleOf(::InMemoryUserRepository) bind UserRepository::class
}