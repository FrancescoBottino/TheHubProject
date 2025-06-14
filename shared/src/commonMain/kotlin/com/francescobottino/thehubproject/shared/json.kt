package com.francescobottino.thehubproject.shared

import kotlinx.serialization.json.Json

val mainJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    allowStructuredMapKeys = true
    encodeDefaults = true
    classDiscriminator = "_type"
}