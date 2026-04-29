package com.personalvault.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Generates and stores a 32-byte random passphrase for SQLCipher inside
 * EncryptedSharedPreferences, backed by the Android Keystore.
 * The passphrase never leaves the device and is only readable by this app.
 */
object DatabasePassphrase {

    private const val PREFS_NAME = "vault_secure_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase_hex"

    fun getOrCreate(context: Context): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existing = prefs.getString(KEY_PASSPHRASE, null)
        if (existing != null) return hexToBytes(existing)

        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        prefs.edit().putString(KEY_PASSPHRASE, bytesToHex(bytes)).apply()
        return bytes
    }

    private fun bytesToHex(bytes: ByteArray): String =
        bytes.joinToString("") { "%02x".format(it) }

    private fun hexToBytes(hex: String): ByteArray {
        val out = ByteArray(hex.length / 2)
        for (i in out.indices) {
            val j = i * 2
            out[i] = ((Character.digit(hex[j], 16) shl 4) +
                Character.digit(hex[j + 1], 16)).toByte()
        }
        return out
    }
}
