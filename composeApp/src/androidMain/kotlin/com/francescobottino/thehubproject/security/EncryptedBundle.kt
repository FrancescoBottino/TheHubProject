package com.francescobottino.thehubproject.security // Adjust as needed

import kotlinx.serialization.Serializable

@Serializable
internal data class EncryptedBundle(
    val encryptedAesKeyB64: String, // RSA-encrypted AES key, Base64 encoded
    val ivB64: String,              // IV for AES GCM, Base64 encoded
    val ciphertextB64: String       // AES-encrypted secret, Base64 encoded
)