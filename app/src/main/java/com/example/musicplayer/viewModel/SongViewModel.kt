package com.example.musicplayer.viewModel


import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.Song
import com.example.musicplayer.repository.Repository
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

class SongViewModel(context: Context) :
    ViewModel() {
    private var _song: MutableStateFlow<MutableList<Song>> = MutableStateFlow(mutableListOf())
    val song get() = _song
    private var _shouldBeClear = MutableStateFlow(false)
    val shouldBeClear get() = _shouldBeClear
    private var _repeatMode = 1
    val repeatMode get() = _repeatMode  //0 for for repeat one, 1 for repeat all , 2 for shuffle
    lateinit var player: ExoPlayer
    init {
        _song.value = Repository.getAllDeviceSongs(context)
    }
    fun setRepeatMode(mode: Int) {
        _repeatMode = mode
    }
    fun refreshSong(context: Context){
        _song.value = Repository.getAllDeviceSongs(context)
    }
    override fun onCleared() {
        if (!player.isPlaying) {
            _shouldBeClear.value = true
        }
        super.onCleared()
    }
}