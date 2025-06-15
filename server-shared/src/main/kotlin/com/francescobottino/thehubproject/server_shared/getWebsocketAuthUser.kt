package com.francescobottino.thehubproject.server_shared

import com.francescobottino.thehubproject.server_shared.auth.JwtConfig
import com.francescobottino.thehubproject.server_shared.auth.USER_ID_CLAIM
import com.francescobottino.thehubproject.server_shared.data.UserRepository
import com.francescobottino.thehubproject.server_shared.model.User
import com.francescobottino.thehubproject.shared.mainJson
import com.francescobottino.thehubproject.shared.model.WebsocketAuthRequest
import com.francescobottino.thehubproject.shared.model.WebsocketAuthResponse
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