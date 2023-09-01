package com.example.musicplayer.viewModel


import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import com.example.musicplayer.dataStore.PreferencesKeys
import com.example.musicplayer.model.Song
import com.example.musicplayer.repository.Repository
import com.example.musicplayer.service.PlayerService
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SongViewModel(context: Context, private val dataStore: DataStore<Preferences>) :
    ViewModel() {
    private var _song: MutableStateFlow<MutableList<Song>> = MutableStateFlow(mutableListOf())
    val song get() = _song
    private var _shouldBeClear = MutableStateFlow(false)
    val shouldBeClear get() = _shouldBeClear
    private var _repeatMode = 1
    val repeatMode get() = _repeatMode  //0 for for repeat one, 1 for repeat all , 2 for shuffle

    lateinit var player: ExoPlayer
    private var _firstTime: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val firstTime get() = _firstTime

    init {
        _song.value = Repository.getAllDeviceSongs(context)
    }

    override fun onCleared() {
        if (!player.isPlaying) {
            _shouldBeClear.value = true
        }
        super.onCleared()
    }

    fun setRepeatMode(mode: Int) {
        _repeatMode = mode
    }

    suspend fun saveToDataStore(
        position: Int,
        title: String,
        artist: String,
        url: String,
        artworkUri: String?
    ) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.position] = position.toString()
            settings[PreferencesKeys.title] = title
            settings[PreferencesKeys.artist] = artist
            settings[PreferencesKeys.url] = url
            artworkUri?.let {
                settings[PreferencesKeys.artworkUri] = artworkUri
            }
        }
    }

    val positionDS: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preferences ->
        val numberOfItem = preferences[PreferencesKeys.position] ?: "1"
        numberOfItem
    }
    val titleDS: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preferences ->
        val numberOfItem = preferences[PreferencesKeys.title] ?: ""
        numberOfItem
    }
    val artistDS: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preferences ->
        val numberOfItem = preferences[PreferencesKeys.artist] ?: ""
        numberOfItem
    }
    val urlDS: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preferences ->
        val numberOfItem = preferences[PreferencesKeys.url] ?: ""
        numberOfItem
    }
    val artworkUriDS: Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preferences ->
        val numberOfItem = preferences[PreferencesKeys.artworkUri] ?: ""
        numberOfItem
    }
}