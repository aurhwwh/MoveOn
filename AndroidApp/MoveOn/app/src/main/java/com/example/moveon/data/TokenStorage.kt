package com.example.moveon.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object TokenStorage {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveTokens(access: String, refresh: String) {
        prefs.edit {
            putString("access", access)
                .putString("refresh", refresh)
        }
    }

    fun getAccess(): String? {
        return prefs.getString("access", null)
    }

    fun getRefresh(): String? {
        return prefs.getString("refresh", null)
    }

    fun clear() {
        prefs.edit { clear() }
    }
}