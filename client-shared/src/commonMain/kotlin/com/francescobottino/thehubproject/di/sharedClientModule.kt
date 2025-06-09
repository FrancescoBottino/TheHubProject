package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.repo.InMemoryUserRepository
import com.francescobottino.thehubproject.repo.UserRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val sharedClientModule = DI.Module("sharedClientModule") {
    bindSingleton<UserRepository> { InMemoryUserRepository() }
}