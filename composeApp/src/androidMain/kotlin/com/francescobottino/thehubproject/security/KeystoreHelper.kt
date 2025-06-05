package com.francescobottino.thehubproject.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

object KeystoreHelper {

    private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    // You can use a more generic alias if this key is only for wrapping other keys
    private const val RSA_KEY_ALIAS = "com.francescobottino.thehubproject.rsawrappingkey"
    // Recommended: OAEP for RSA encryption
    private const val RSA_CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply {
            load(null)
        }
    }

    @Throws(
        NoSuchProviderException::class, NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class, KeyStoreException::class,
        UnrecoverableEntryException::class, CertificateException::class, IOException::class
    )
    private fun getOrCreateRsaKeyPair(): KeyPair {
        return if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEYSTORE_PROVIDER
            )

            val specBuilder = KeyGenParameterSpec.Builder(
                RSA_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512) // Required for OAEP
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP) // Use OAEP
                .setKeySize(2048) // Explicitly set key size

            // Optional: User authentication requirements (consider for sensitive keys)
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //     specBuilder.setUserAuthenticationRequired(true)
            //     // For biometric prompt immediately or specific timeout:
            //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //          specBuilder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL)
            //     } else {
            //          specBuilder.setUserAuthenticationValidityDurationSeconds(30) // Key usable for 30s after auth
            //     }
            // }

            keyPairGenerator.initialize(specBuilder.build())
            keyPairGenerator.generateKeyPair()
        } else {
            val entry = keyStore.getEntry(RSA_KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
            entry?.let { KeyPair(it.certificate.publicKey, it.privateKey) }
                ?: throw KeyStoreException("Failed to retrieve RSA key pair, entry not found or not a PrivateKeyEntry for alias: $RSA_KEY_ALIAS")
        }
    }

    internal fun getRsaPublicKey(): PublicKey {
        return getOrCreateRsaKeyPair().public
    }

    private fun getRsaPrivateKey(): PrivateKey {
        return getOrCreateRsaKeyPair().private
    }

    @Throws(
        NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class,
        BadPaddingException::class, IllegalBlockSizeException::class, KeyStoreException::class,
        UnrecoverableEntryException::class, CertificateException::class, IOException::class,
        NoSuchProviderException::class, InvalidAlgorithmParameterException::class
    )
    fun rsaEncrypt(data: ByteArray, publicKey: PublicKey = getRsaPublicKey()): ByteArray {
        if (data.isEmpty()) return ByteArray(0)
        val cipher = Cipher.getInstance(RSA_CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    @Throws(
        NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class,
        BadPaddingException::class, IllegalBlockSizeException::class, KeyStoreException::class,
        UnrecoverableEntryException::class, CertificateException::class, IOException::class,
        NoSuchProviderException::class, InvalidAlgorithmParameterException::class
    )
    fun rsaDecrypt(encryptedData: ByteArray, privateKey: PrivateKey = getRsaPrivateKey()): ByteArray {
        if (encryptedData.isEmpty()) return ByteArray(0)
        val cipher = Cipher.getInstance(RSA_CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(encryptedData)
    }

    fun deleteRsaKey() {
        if (keyStore.containsAlias(RSA_KEY_ALIAS)) {
            keyStore.deleteEntry(RSA_KEY_ALIAS)
        }
    }
}