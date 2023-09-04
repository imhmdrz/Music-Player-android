package com.example.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentMainBinding
import com.example.musicplayer.databinding.SongItemBinding
import com.example.musicplayer.model.Song
import com.example.musicplayer.service.PlayerService
import com.example.musicplayer.utils.readableTime
import com.example.musicplayer.viewModel.SongViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata

class RvAdapter(private val context: Context, private var viewModel: SongViewModel) :
    ListAdapter<Song, RvAdapter.SongViewHolder>(DiffCallback) {
    class SongViewHolder(
        private val binding: SongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.apply {
                title.text = song.title
                artist.text = song.artistName
                duration.text = readableTime(song.duration.toLong())
                if (song.artworkUri != null) {
                    albumPhoto.load(song.artworkUri) {
                        placeholder(android.R.drawable.ic_menu_report_image)
                        error(android.R.drawable.ic_menu_report_image)
                    }
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
        holder.itemView.setOnClickListener {
            playMusic(position)
            context.startService(Intent(context.applicationContext, PlayerService::class.java))
            PlayerService.isStarted = true
        }
    }

    fun playMusic(position: Int) {
        if (!viewModel.player.isPlaying) {
            viewModel.player.setMediaItems(getMusic(), position, 0)
        } else {
            viewModel.player.pause()
            viewModel.player.seekTo(position, 0)
        }
        viewModel.player.prepare()
        viewModel.player.play()
    }

    private fun getMusic(): MutableList<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()
        for (song in currentList) {
            val media = MediaItem.Builder().setUri(song.uri).setMediaMetadata(
                    MediaMetadata.Builder().setTitle(song.title).setArtist(song.artistName)
                        .setArtworkUri(song.artworkUri).build()
                ).build()
            mediaItems.add(media)
        }
        return mediaItems
    }
}