package com.francescobottino.thehubproject.server

import com.francescobottino.thehubproject.server.auth.JwtConfigImpl
import com.francescobottino.thehubproject.server.auth.configureJwt
import com.francescobottino.thehubproject.server.auth.configureRoutingAuth
import com.francescobottino.thehubproject.server.data.ExposedUserRepository
import com.francescobottino.thehubproject.server_features.core.auth.JwtConfig
import com.francescobottino.thehubproject.server_features.core.data.UserRepository
import com.francescobottino.thehubproject.shared_features.core.mainJson
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
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
        host = "0.0.0.0",
        port = Envs.SERVER_PORT,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val database = configureDatabase()
    val jwtConfig = JwtConfigImpl()
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { database }
                single<JwtConfig> { jwtConfig }
                single<UserRepository> { ExposedUserRepository(get()) }
            },
        )
    }

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
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        allowCredentials = false
    }

    install(Authentication) {
        configureJwt(jwtConfig)
    }

    routing {
        configureRoutingAuth()
        configureRoutingUser()
        configureRoutingGames()

        get("/") { call.respondText("Hello World!") }
    }
}