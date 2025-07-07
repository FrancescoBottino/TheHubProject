package com.francescobottino.thehubproject.client_features.core.navigation

import cafe.adriel.voyager.core.screen.Screen

interface Deeplink {
    fun getPath(): String
    fun getDestination(): Screen
}