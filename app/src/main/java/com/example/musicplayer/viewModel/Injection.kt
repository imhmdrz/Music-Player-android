package com.example.musicplayer.viewModel

import android.content.Context
import androidx.lifecycle.ViewModelProvider

object Injection {
    fun provideSongViewModelFactory(
        context: Context
    ): ViewModelProvider.Factory {
        return SongViewModelFactory(context)
    }
}