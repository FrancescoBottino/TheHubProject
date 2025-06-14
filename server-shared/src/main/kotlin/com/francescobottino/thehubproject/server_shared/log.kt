package com.francescobottino.thehubproject.server_shared

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.logging.*

val RoutingContext.log: Logger
    get() = call.application.environment.log

val DefaultWebSocketServerSession.log: Logger
    get() = call.application.environment.log