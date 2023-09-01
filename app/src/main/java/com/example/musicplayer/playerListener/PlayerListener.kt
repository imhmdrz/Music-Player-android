package com.example.musicplayer.playerListener

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.viewModel.SongViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects

class PlayerListener(
    private var binding: FragmentPlayerBinding,
    var viewModel: SongViewModel
) : Player.Listener {
    private fun readableTime(dur: Long): String {
        val hrs = dur / (1000 * 60 * 60)
        val min = (dur % (1000 * 60 * 60)) / (1000 * 60)
        val sec = (((dur % (1000 * 60 * 60)) % (1000 * 60 * 60)) % (1000 * 60)) / 1000
        return if (hrs < 1) {
            ("$min:$sec")
        } else {
            ("$hrs:$min:$sec")
        }
    }
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        binding.apply {
            progressTxt.text = readableTime(viewModel.player.currentPosition)
            seekBar.progress = viewModel.player.currentPosition.toInt()
            titlePlayer.text = mediaItem?.mediaMetadata?.title
            titleArtist.text = mediaItem?.mediaMetadata?.artist
            if (viewModel.player.isPlaying) {
                binding.playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
            } else {
                binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
            }
            albumPhotoPlayer.load(viewModel.player.currentMediaItem?.mediaMetadata?.artworkUri) {
                placeholder(R.drawable.baseline_music_note_24)
                error(R.drawable.baseline_music_note_24)
            }
        }
    }
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == ExoPlayer.STATE_READY) {
            binding.apply {
                progressTxt.text = readableTime(viewModel.player.currentPosition)
                seekBar.progress = viewModel.player.currentPosition.toInt()
                seekBar.max = viewModel.player.duration.toInt()
                progressDuration.text = readableTime(viewModel.player.duration)
                titlePlayer.text = viewModel.player.currentMediaItem?.mediaMetadata?.title
                titleArtist.text = viewModel.player.currentMediaItem?.mediaMetadata?.artist
                playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
                if (!viewModel.player.isPlaying) {
                    viewModel.player.play()
                }
            }
        } else {
            binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
        }
    }
}