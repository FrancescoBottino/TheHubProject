package com.francescobottino.thehubproject.client_shared.network

import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.shared.mainJson
import com.francescobottino.thehubproject.shared.model.WebsocketAuthRequest
import com.francescobottino.thehubproject.shared.model.WebsocketAuthResponse
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import org.koin.mp.KoinPlatform

public suspend fun HttpClient.webSocketSessionAuth(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): DefaultClientWebSocketSession {
    val token = KoinPlatform.getKoin().get<AuthRepository>().getToken()!!
    val authRequest = WebsocketAuthRequest(token)
    val authRequestString = mainJson.encodeToString(authRequest)

    val session = webSocketSession(urlString, block)

    session.send(Frame.Text(authRequestString))

    val authResponseFrame = session.incoming.receive()
    val authResponseString = (authResponseFrame as Frame.Text).readText()
    val authResponse = mainJson.decodeFromString<WebsocketAuthResponse>(authResponseString)

    require(authResponse.isSuccess)

    return session
}