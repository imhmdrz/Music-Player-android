package com.example.musicplayer.playerListener

import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentMainBinding
import com.example.musicplayer.viewModel.SongViewModel
import com.google.android.exoplayer2.DeviceInfo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.Objects

class MainListener(
    private var binding: FragmentMainBinding,
    var viewModel: SongViewModel
) : Player.Listener {
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        binding.apply {
            if (viewModel.player.isPlaying) {
                playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
            } else {
                playPauseBtn.setIconResource(R.drawable.ic_play_circle)
            }
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        binding.apply {
            if (viewModel.player.isPlaying) {
                playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
            } else {
                playPauseBtn.setIconResource(R.drawable.ic_play_circle)
            }
        }
    }
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == ExoPlayer.STATE_READY) {
            binding.apply {
                playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
                if (!viewModel.player.isPlaying) {
                    viewModel.player.play()
                }
            }
        } else {
            binding.apply {
                playPauseBtn.setIconResource(R.drawable.ic_play_circle)
            }
        }
    }
}