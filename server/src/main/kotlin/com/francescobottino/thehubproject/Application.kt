package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.auth.configureSecurity
import com.francescobottino.thehubproject.data.UserRepository
import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.kodein.di.bindSingleton
import org.kodein.di.ktor.di
import kotlin.time.Duration.Companion.seconds

fun main() {
    dotenv {
        ignoreIfMissing = true // Don't fail if .env is not found (important for Render)
        systemProperties = true // Also load into system properties (optional)
    }

    embeddedServer(
        factory = Netty,
        port = System.getenv("PORT")?.toIntOrNull() ?: Config.DEBUG_PORT,
        host = System.getenv("HOST") ?: "0.0.0.0",
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

        allowCredentials = false

        if(Config.IS_DEBUG) {
            anyHost()
        } else {
            allowHost(Config.PROD_ENDPOINT, schemes = listOf("http", "https"))
        }
    }

    val database = configureDatabase()

    di {
        bindSingleton<UserRepository> { configureUserRepository(database) }
    }

    configureSecurity()
    configureUserRouting()

    // debug routing,
    // todo remove
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