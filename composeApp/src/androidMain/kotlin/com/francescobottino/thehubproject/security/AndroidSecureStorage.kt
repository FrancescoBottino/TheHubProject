package com.francescobottino.thehubproject.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import kotlinx.serialization.json.Json
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AndroidSecureStorage(context: Context): SecureStorage {

    private val sharedPreferences = context.getSharedPreferences("HybridSecureStorage", Context.MODE_PRIVATE)

    // AES Configuration
    private companion object {
        const val AES_KEY_SIZE = 256 // bits
        const val AES_MODE = "AES/GCM/NoPadding"
        const val GCM_IV_LENGTH_BYTES = 12 // Standard GCM IV length
        const val GCM_TAG_LENGTH_BITS = 128 // Standard GCM Auth Tag length
    }

    override suspend fun saveSecret(key: String, secret: String) {
        try {
            // 1. Generate a new random AES key for this specific secret
            val aesKeyGenerator = KeyGenerator.getInstance("AES")
            aesKeyGenerator.init(AES_KEY_SIZE)
            val aesKey: SecretKey = aesKeyGenerator.generateKey()

            // 2. Encrypt the actual secret using this AES key with GCM
            val aesCipher = Cipher.getInstance(AES_MODE)
            // Generate a random IV
            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            SecureRandom().nextBytes(iv)
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec)
            val encryptedSecretBytes: ByteArray = aesCipher.doFinal(secret.toByteArray(Charsets.UTF_8))

            // 3. Encrypt the generated AES key using the RSA Public Key from KeystoreHelper
            val rsaPublicKey = KeystoreHelper.getRsaPublicKey() // Get public key once
            val encryptedAesKeyBytes: ByteArray = KeystoreHelper.rsaEncrypt(aesKey.encoded, rsaPublicKey)

            // 4. Create the bundle
            val bundle = EncryptedBundle(
                encryptedAesKeyB64 = Base64.encodeToString(encryptedAesKeyBytes, Base64.NO_WRAP),
                ivB64 = Base64.encodeToString(iv, Base64.NO_WRAP),
                ciphertextB64 = Base64.encodeToString(encryptedSecretBytes, Base64.NO_WRAP)
            )

            // 5. Serialize the bundle to JSON and save to SharedPreferences
            val bundleJsonString = Json.encodeToString(bundle)
            sharedPreferences.edit { putString(key, bundleJsonString) }

        } catch (e: Exception) {
            // Log error or throw a custom exception
            // Consider how to handle failures: clear partially written data?
            // For simplicity, logging here. In production, more robust error handling is needed.
            android.util.Log.e("AndroidSecureStorage", "Failed to save secret for key: $key", e)
            // Optionally, rethrow or throw a custom exception to inform the caller
            // throw SecureStorageException("Failed to save secret", e)
        }
    }

    override suspend fun getSecret(key: String): String? {
        try {
            val bundleJsonString = sharedPreferences.getString(key, null) ?: return null

            // 1. Deserialize the bundle from JSON
            val bundle = Json.decodeFromString<EncryptedBundle>(bundleJsonString)

            val encryptedAesKeyBytes = Base64.decode(bundle.encryptedAesKeyB64, Base64.NO_WRAP)
            val iv = Base64.decode(bundle.ivB64, Base64.NO_WRAP)
            val encryptedSecretBytes = Base64.decode(bundle.ciphertextB64, Base64.NO_WRAP)

            // 2. Decrypt the AES key using the RSA Private Key from KeystoreHelper
            val decryptedAesKeyBytes: ByteArray = KeystoreHelper.rsaDecrypt(encryptedAesKeyBytes)
            val aesKey: SecretKey = SecretKeySpec(decryptedAesKeyBytes, 0, decryptedAesKeyBytes.size, "AES")

            // 3. Decrypt the secret using the AES key and IV
            val aesCipher = Cipher.getInstance(AES_MODE)
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmParameterSpec)
            val decryptedSecretBytes: ByteArray = aesCipher.doFinal(encryptedSecretBytes)

            return String(decryptedSecretBytes, Charsets.UTF_8)

        } catch (e: Exception) {
            android.util.Log.e("AndroidSecureStorage", "Failed to get secret for key: $key", e)
            // If decryption fails (e.g., key invalidated, data tampered), return null or throw
            // Consider deleting the corrupted entry if appropriate: clearSecret(key)
            return null // Or throw SecureStorageException("Failed to retrieve secret", e)
        }
    }

    override suspend fun clearSecret(key: String) {
        sharedPreferences.edit { remove(key) }
    }
}