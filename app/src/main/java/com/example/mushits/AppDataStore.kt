package com.example.mushits

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appDataStore by preferencesDataStore("app_data_store")

object AppPrefs {
    val LAST_SONG_ID = longPreferencesKey("last_song_id")
    val LAST_POSITION = longPreferencesKey("last_position")
}

suspend fun saveLastSong(context: Context, songId: Long, position: Long) {
    context.appDataStore.edit { prefs ->
        prefs[AppPrefs.LAST_SONG_ID] = songId
        prefs[AppPrefs.LAST_POSITION] = position
    }
}

fun getLastSongId(context: Context): Flow<Long> =
    context.appDataStore.data.map { prefs ->
        prefs[AppPrefs.LAST_SONG_ID] ?: -1
    }

fun getLastPosition(context: Context): Flow<Long> =
    context.appDataStore.data.map { prefs ->
        prefs[AppPrefs.LAST_POSITION] ?: 0L
    }
