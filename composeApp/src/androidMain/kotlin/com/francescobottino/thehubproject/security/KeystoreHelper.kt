package com.francescobottino.thehubproject.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher

object KeystoreHelper {

    private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val RSA_KEY_ALIAS = "com.francescobottino.thehubproject.security.rsawrappingkey"
    private const val RSA_CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"

    private val rsaKeyPair: KeyPair by lazy {
        getOrCreateRsaKeyPair()
    }

    private fun getRsaPublicKey() = rsaKeyPair.public
    private fun getRsaPrivateKey() = rsaKeyPair.private

    private fun getOrCreateRsaKeyPair(): KeyPair {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }

        return if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEYSTORE_PROVIDER
            )
            val spec = KeyGenParameterSpec.Builder(
                RSA_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                // Use the compatible padding spec.
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                // NO LONGER NEED .setDigests() for this padding, removing the source of the error.
                .setKeySize(2048)
                .build()
            keyPairGenerator.initialize(spec)
            keyPairGenerator.generateKeyPair()
        } else {
            val entry = keyStore.getEntry(RSA_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            KeyPair(entry.certificate.publicKey, entry.privateKey)
        }
    }

    fun rsaEncrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(RSA_CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getRsaPublicKey())
        return cipher.doFinal(data)
    }

    fun rsaDecrypt(encryptedData: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(RSA_CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getRsaPrivateKey())
        return cipher.doFinal(encryptedData)
    }
}