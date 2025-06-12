package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.auth.configureRoutingAuth
import com.francescobottino.thehubproject.auth.configureSecurity
import com.francescobottino.thehubproject.data.ExposedUserRepository
import com.francescobottino.thehubproject.data.UserRepository
import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
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

    embeddedServer(
        factory = Netty,
        host = Envs.HOST,
        port = Envs.PORT,
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

    val database = configureDatabase()

    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { database }
                single<UserRepository> { ExposedUserRepository(get()) }
            },
        )
    }

    configureSecurity()

    routing {
        configureRoutingAuth()
        configureRoutingUser()
        configureRoutingGames()

        get("/") { call.respondText("Hello World!") }
    }
}