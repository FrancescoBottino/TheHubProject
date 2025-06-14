package com.francescobottino.thehubproject.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import com.francescobottino.thehubproject.client_shared.security.SecureStorage
import kotlinx.serialization.json.Json
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AndroidSecureStorage(context: Context) : SecureStorage {

    private val sharedPreferences = context.getSharedPreferences("HybridSecureStorage", Context.MODE_PRIVATE)

    private companion object {
        const val AES_KEY_SIZE = 256
        const val AES_MODE = "AES/GCM/NoPadding"
        const val GCM_IV_LENGTH_BYTES = 12
        const val GCM_TAG_LENGTH_BITS = 128
    }

    override suspend fun saveSecret(key: String, secret: String) {
        try {
            val aesKeyGenerator = KeyGenerator.getInstance("AES")
            aesKeyGenerator.init(AES_KEY_SIZE)
            val aesKey: SecretKey = aesKeyGenerator.generateKey()

            val encryptedAesKeyBytes: ByteArray = KeystoreHelper.rsaEncrypt(aesKey.encoded)

            val aesCipher = Cipher.getInstance(AES_MODE)
            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            SecureRandom().nextBytes(iv)
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec)
            val encryptedSecretBytes: ByteArray = aesCipher.doFinal(secret.toByteArray(Charsets.UTF_8))

            val bundle = EncryptedBundle(
                encryptedAesKeyB64 = encryptedAesKeyBytes.encodeBase64(),
                ivB64 = iv.encodeBase64(),
                ciphertextB64 = encryptedSecretBytes.encodeBase64()
            )
            val bundleJsonString = Json.encodeToString(bundle)
            sharedPreferences.edit(commit = true) { putString(key, bundleJsonString) }

        } catch (e: Exception) {
            android.util.Log.e("AndroidSecureStorage", "Failed to save secret for key: $key", e)
            throw SecureStorageException("Failed to save secret", e)
        }
    }

    override suspend fun getSecret(key: String): String? {
        try {
            val bundleJsonString = sharedPreferences.getString(key, null) ?: return null
            val bundle = Json.decodeFromString<EncryptedBundle>(bundleJsonString)

            val decryptedAesKeyBytes: ByteArray = KeystoreHelper.rsaDecrypt(bundle.encryptedAesKeyB64.decodeBase64())

            val aesKey: SecretKey = SecretKeySpec(decryptedAesKeyBytes, 0, decryptedAesKeyBytes.size, "AES")

            val iv = bundle.ivB64.decodeBase64()
            val encryptedSecretBytes = bundle.ciphertextB64.decodeBase64()
            val aesCipher = Cipher.getInstance(AES_MODE)
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmParameterSpec)
            val decryptedSecretBytes: ByteArray = aesCipher.doFinal(encryptedSecretBytes)

            return String(decryptedSecretBytes, Charsets.UTF_8)

        } catch (e: Exception) {
            android.util.Log.e("AndroidSecureStorage", "Failed to get secret for key: $key. This may be due to a key invalidation.", e)
            clearSecret(key)
            return null
        }
    }

    override suspend fun clearSecret(key: String) {
        sharedPreferences.edit(commit = true) { remove(key) }
    }

    private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)
    private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
}