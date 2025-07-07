package com.francescobottino.thehubproject.server_features.core

import com.francescobottino.thehubproject.server_features.core.auth.JwtConfig
import com.francescobottino.thehubproject.server_features.core.auth.USER_ID_CLAIM
import com.francescobottino.thehubproject.server_features.core.data.UserRepository
import com.francescobottino.thehubproject.server_features.core.model.User
import com.francescobottino.thehubproject.shared_features.core.mainJson
import com.francescobottino.thehubproject.shared_features.core.model.WebsocketAuthRequest
import com.francescobottino.thehubproject.shared_features.core.model.WebsocketAuthResponse
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject

context(route: Route)
suspend fun DefaultWebSocketServerSession.getWebsocketAuthUserId(): String? {
    val jwt by call.inject<JwtConfig>()

    //todo timeout?

    val token = incoming.receive()
        .let { it as? Frame.Text }
        ?.readText()
        ?.let { runCatching { mainJson.decodeFromString<WebsocketAuthRequest>(it) } }
        ?.getOrNull()
        ?.token
        ?: return null

    val userId = runCatching { jwt.getVerifier().verify(token).claims[USER_ID_CLAIM]?.asString() }
        .getOrNull()
        ?: return null

    send(Frame.Text(mainJson.encodeToString(WebsocketAuthResponse(isSuccess = true))))

    return userId
}

context(route: Route)
suspend fun DefaultWebSocketServerSession.getWebsocketAuthUser(): User? {
    val userRepository by call.inject<UserRepository>()
    return getWebsocketAuthUserId()?.let { userRepository.findById(it) }
}