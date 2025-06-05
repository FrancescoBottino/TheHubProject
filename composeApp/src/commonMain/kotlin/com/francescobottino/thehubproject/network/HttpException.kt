package com.francescobottino.thehubproject.network

import io.ktor.client.request.*
import io.ktor.client.statement.*

class HttpException(val request: HttpRequest, val response: HttpResponse, override val cause: Exception): Exception()