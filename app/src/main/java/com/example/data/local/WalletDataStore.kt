package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.WalletSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.walletDataStore: DataStore<Preferences> by preferencesDataStore(name = "paktok_wallet")

class WalletDataStore(private val context: Context) {

    private object Keys {
        val TOTAL_COINS = intPreferencesKey("total_coins")
        val WATCH_ELAPSED_MS = longPreferencesKey("watch_elapsed_ms")
        val TOTAL_WATCH_SECONDS = longPreferencesKey("total_watch_seconds")
        val FOLLOWERS_COUNT = intPreferencesKey("followers_count")
        val PROFILE_HANDLE = stringPreferencesKey("profile_handle")
        val PROFILE_NAME = stringPreferencesKey("profile_name")
    }

    val walletFlow: Flow<WalletSnapshot> = context.walletDataStore.data.map { prefs ->
        WalletSnapshot(
            totalCoins = prefs[Keys.TOTAL_COINS] ?: 0,
            watchElapsedMs = prefs[Keys.WATCH_ELAPSED_MS] ?: 0L,
            totalWatchSeconds = prefs[Keys.TOTAL_WATCH_SECONDS] ?: 0L,
            followersCount = prefs[Keys.FOLLOWERS_COUNT] ?: 0,
            profileHandle = prefs[Keys.PROFILE_HANDLE] ?: "@PakCreator",
            profileName = prefs[Keys.PROFILE_NAME] ?: "PakTok Star"
        )
    }

    suspend fun saveWatchProgress(
        watchElapsedMs: Long,
        coinsEarnedDelta: Int,
        watchedSecondsDelta: Long
    ) {
        context.walletDataStore.edit { prefs ->
            val currentCoins = prefs[Keys.TOTAL_COINS] ?: 0
            val currentSeconds = prefs[Keys.TOTAL_WATCH_SECONDS] ?: 0L
            prefs[Keys.TOTAL_COINS] = currentCoins + coinsEarnedDelta
            prefs[Keys.WATCH_ELAPSED_MS] = watchElapsedMs
            prefs[Keys.TOTAL_WATCH_SECONDS] = currentSeconds + watchedSecondsDelta
        }
    }

    suspend fun deductCoins(amount: Int): Boolean {
        var success = false
        context.walletDataStore.edit { prefs ->
            val currentCoins = prefs[Keys.TOTAL_COINS] ?: 0
            if (amount > 0 && currentCoins >= amount) {
                prefs[Keys.TOTAL_COINS] = currentCoins - amount
                success = true
            }
        }
        return success
    }

    suspend fun updateProfile(name: String, handle: String) {
        val sanitizedHandle = if (handle.startsWith("@")) handle else "@$handle"
        context.walletDataStore.edit { prefs ->
            prefs[Keys.PROFILE_NAME] = name.trim().ifEmpty { "PakTok Star" }
            prefs[Keys.PROFILE_HANDLE] = sanitizedHandle.trim().ifEmpty { "@PakCreator" }
        }
    }
}
