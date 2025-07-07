package com.francescobottino.thehubproject.games.tictactoe.client

import com.francescobottino.thehubproject.client_features.core.navigation.Deeplink
import com.francescobottino.thehubproject.client_features.core.navigation.DeeplinkParser
import com.francescobottino.thehubproject.games.tictactoe.client.screens.join_room.JoinRoomScreen

object TicTacToeDeepLink {
    private const val BASE_PATH = "tictactoe"
    private const val INVITE = "invite"

    data class Invite(
        val roomId: String,
    ): Deeplink {
        override fun getPath() = basePath + roomId
        override fun getDestination() = JoinRoomScreen(roomId)

        companion object Parser: DeeplinkParser<Invite> {
            override val basePath: String
                get() = "$BASE_PATH/$INVITE/"
            override fun getFromPath(deepLinkPath: String): Invite? {
                return Invite(deepLinkPath.removePrefix(basePath))
            }
        }
    }
}