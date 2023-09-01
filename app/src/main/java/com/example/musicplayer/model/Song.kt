package com.example.musicplayer.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val duration: Int,
    val uri: Uri?,
    val artworkUri: Uri?,
    val albumName: String,
    val artistId: Int,
    val artistName: String
)

