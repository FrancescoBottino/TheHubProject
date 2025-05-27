package com.francescobottino.hexchessgame

import com.francescobottino.hexchessgame.ServerConstants.CORS_ALLOWED_HOSTS
import com.francescobottino.hexchessgame.ServerConstants.CORS_ALLOWED_SCHEMES
import com.francescobottino.hexchessgame.ServerConstants.CORS_ALLOW_ANY_HOST
import com.francescobottino.hexchessgame.ServerConstants.CORS_ALLOW_CREDENTIALS
import com.francescobottino.hexchessgame.ServerConstants.DEV_SERVER_PORT
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: DEV_SERVER_PORT

    embeddedServer(
        factory = Netty,
        port = port,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // Install CORS
    install(CORS) {
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        allowMethod(io.ktor.http.HttpMethod.Patch)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowHeader(io.ktor.http.HttpHeaders.ContentType)

        allowCredentials = CORS_ALLOW_CREDENTIALS

        if (CORS_ALLOW_ANY_HOST) {
            anyHost()
        } else {
            for (host in CORS_ALLOWED_HOSTS) {
                allowHost(host, schemes = CORS_ALLOWED_SCHEMES)
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        webSocket("/ws/echo") {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                    application.environment.log.info("Echo WS: Received from client: '$receivedText'")
                    send(Frame.Text("Server echoing: $receivedText"))
                }
            }
        }
    }
}