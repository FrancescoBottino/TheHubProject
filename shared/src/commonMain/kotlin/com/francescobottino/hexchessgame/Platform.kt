package com.francescobottino.hexchessgame

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform