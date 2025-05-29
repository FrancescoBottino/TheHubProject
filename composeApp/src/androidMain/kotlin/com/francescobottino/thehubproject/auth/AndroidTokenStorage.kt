package com.francescobottino.thehubproject.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit

class AndroidTokenStorage(context: Context): TokenStorage {
    private val masterKey = MasterKey
        .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        /* context = */ context,
        /* fileName = */ SHARED_PREF_NAME,
        /* masterKey = */ masterKey,
        /* prefKeyEncryptionScheme = */ EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        /* prefValueEncryptionScheme = */ EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private companion object {
        const val SHARED_PREF_NAME = "thehub_auth_shared_prefs"
        const val KEY_AUTH_TOKEN = "auth_token"
    }

    override suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit { putString(KEY_AUTH_TOKEN, token) }
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit { remove(KEY_AUTH_TOKEN) }
    }
}