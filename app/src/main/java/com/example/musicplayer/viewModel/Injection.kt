package com.example.musicplayer.viewModel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider

object Injection {
    fun provideSongViewModelFactory(
        context: Context,
        dataStore: DataStore<Preferences>
    ): ViewModelProvider.Factory {
        return SongViewModelFactory(context,dataStore)
    }
}