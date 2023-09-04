package com.example.musicplayer.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.musicplayer.model.Song
import java.net.URI

object Repository {
    private const val ID = 0
    private const val TITLE = 1
    private const val DURATION = 2
    private const val ALBUM_ID = 3
    private const val ALBUM = 4
    private const val ARTIST_ID = 5
    private const val ARTIST = 6
    private val BASE_PROJECTION = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.AudioColumns.ARTIST_ID,
        MediaStore.Audio.AudioColumns.ARTIST,
    )
    private val mAllDeviceSongs = ArrayList<Song>()
    fun getAllDeviceSongs(context: Context): MutableList<Song> {
        val cursor = makeSongCursor(context)
        return getSongs(cursor)
    }
    private fun getSongs(cursor: Cursor?): MutableList<Song> {
        val songs = ArrayList<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val song = getSongFromCursorImpl(cursor)
                if (song.duration > 30000){
                    songs.add(song)
                    mAllDeviceSongs.add(song)
                }

            } while (cursor.moveToNext())
        }

        cursor?.close()

        return songs
    }
    private fun getSongFromCursorImpl(cursor: Cursor): Song {
        val id = cursor.getLong(ID)
        val title = cursor.getString(TITLE)
        val duration = cursor.getInt(DURATION)
        val albumId = cursor.getLong(ALBUM_ID)
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
        val artworkUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
        val albumName = cursor.getString(ALBUM)
        val artistId = cursor.getInt(ARTIST_ID)
        val artistName = cursor.getString(ARTIST)
        return Song(
            id,
            title,
            duration,
            uri,
            artworkUri,
            albumName,
            artistId,
            artistName
        )
    }
    private fun makeSongCursor(context: Context): Cursor? {
        return try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                BASE_PROJECTION, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC"
            )
        } catch (e: SecurityException) {
            null
        }
    }
}