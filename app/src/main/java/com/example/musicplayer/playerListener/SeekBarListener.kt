package com.example.musicplayer.playerListener

import android.widget.SeekBar
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.utils.readableTime
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

class SeekBarListener(private var binding: FragmentPlayerBinding, private val player: ExoPlayer) :
    SeekBar.OnSeekBarChangeListener {
    var progressVal = 0
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        progressVal = progress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (player.playbackState == Player.STATE_READY) {
            seekBar?.progress = progressVal
            binding.progressTxt.text = readableTime(progressVal.toLong())
            player.seekTo(progressVal.toLong())
        }
    }
}