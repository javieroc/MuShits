package com.example.mushits

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mushits.ui.theme.ColorMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appDataStore by preferencesDataStore("app_data_store")

object AppPrefs {
    val COLOR_MODE = stringPreferencesKey("color_mode")
    val LAST_SONG_ID = longPreferencesKey("last_song_id")
    val LAST_POSITION = longPreferencesKey("last_position")
}

suspend fun saveColorMode(context: Context, mode: ColorMode) {
    context.appDataStore.edit { prefs ->
        prefs[AppPrefs.COLOR_MODE] = mode.name
    }
}

fun getColorMode(context: Context): Flow<ColorMode> =
    context.appDataStore.data.map { prefs ->
        val stored = prefs[AppPrefs.COLOR_MODE]
        if (stored != null) {
            try {
                ColorMode.valueOf(stored)
            } catch (e: Exception) {
                ColorMode.MODE1
            }
        } else {
            ColorMode.MODE1
        }
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
