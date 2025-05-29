package com.francescobottino.thehubproject.di

import com.francescobottino.thehubproject.network.ApiService
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

// This module expects TokenStorage to be bound by the platform
val commonModule = DI.Module("commonModule") {
    bindSingleton { ApiService(instance()) }
}