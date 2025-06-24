package com.francescobottino.thehubproject.server

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureRoutingStatic() {
    get("/.well-known/assetlinks.json") {
        // Define the path relative to the resources folder
        val resourcePath = "well-known/assetlinks.json"

        // Read the file from the application's classpath resources
        val assetlinks = application.javaClass.classLoader
            .getResourceAsStream(resourcePath)
            ?.reader()
            ?.readText()

        if (assetlinks != null) {
            // Serve the content with the correct Content-Type
            call.respondText(
                text = assetlinks,
                contentType = ContentType.Application.Json
            )
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Can't get file content")
        }
    }
    get("/well-known/assetlinks.json") {
        // Define the path relative to the resources folder
        val resourcePath = "well-known/assetlinks.json"

        // Read the file from the application's classpath resources
        val assetlinks = application.javaClass.classLoader
            .getResourceAsStream(resourcePath)
            ?.reader()
            ?.readText()

        if (assetlinks != null) {
            // Serve the content with the correct Content-Type
            call.respondText(
                text = assetlinks,
                contentType = ContentType.Application.Json
            )
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Can't get file content")
        }
    }
}