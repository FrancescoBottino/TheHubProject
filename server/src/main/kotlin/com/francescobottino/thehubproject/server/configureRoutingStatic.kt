package com.francescobottino.thehubproject.server

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Routing.configureRoutingStatic() {
    staticResources("/.well-known", "well-known") {
        this.contentType { url ->
            if(url.path.endsWith(".json") || url.path.contains("apple-app-site-association"))
                ContentType.Application.Json
            else
                ContentType.defaultForFilePath(url.path)
        }
    }
}