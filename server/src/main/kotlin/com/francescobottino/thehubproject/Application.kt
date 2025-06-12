package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.auth.configureRoutingAuth
import com.francescobottino.thehubproject.auth.configureSecurity
import com.francescobottino.thehubproject.config.PlatformConfig
import com.francescobottino.thehubproject.data.UserRepository
import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.time.Duration.Companion.seconds

fun main() {
    dotenv {
        ignoreIfMissing = true // Don't fail if .env is not found (important for Render)
        systemProperties = true // Also load into system properties (optional)
    }

    val port = System.getenv("PORT")?.toIntOrNull()
        ?: if(PlatformConfig.isDebug) 8080 else throw Exception("PORT environment variable not set.")

    embeddedServer(
        factory = Netty,
        port = port,
        host = System.getenv("HOST") ?: "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(mainJson)
    }

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(CORS) {
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        allowMethod(io.ktor.http.HttpMethod.Patch)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowHeader(io.ktor.http.HttpHeaders.ContentType)

        allowCredentials = false

        if(PlatformConfig.isDebug) {
            anyHost()
        } else {
            allowHost(PlatformConfig.serverEndpoint, schemes = listOf("https"))
        }
    }

    val database = configureDatabase()

    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<UserRepository> { configureUserRepository(database) }
            },
        )
    }

    configureSecurity()

    routing {
        configureRoutingAuth()
        configureRoutingUser()
        configureRoutingGames()
    }
}