package com.example.musicplayer.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val LAYOUT_PREFERENCES_NAME = "lastMusicPlayed"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCES_NAME
)
object PreferencesKeys {
    val position = stringPreferencesKey("position")
    val title = stringPreferencesKey("title")
    val artist = stringPreferencesKey("artist")
    val url = stringPreferencesKey("url")
    val artworkUri = stringPreferencesKey("artworkUri")
}