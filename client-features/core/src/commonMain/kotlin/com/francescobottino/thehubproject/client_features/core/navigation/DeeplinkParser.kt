package com.francescobottino.thehubproject.client_features.core.navigation

interface DeeplinkParser<T: Deeplink> {
    val basePath: String
    fun getFromPath(deepLinkPath: String): T?
}