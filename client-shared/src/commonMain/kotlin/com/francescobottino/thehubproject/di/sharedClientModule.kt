package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.repo.InMemoryUserRepository
import com.francescobottino.thehubproject.repo.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedClientModule = module {
    singleOf(::InMemoryUserRepository) bind UserRepository::class
}