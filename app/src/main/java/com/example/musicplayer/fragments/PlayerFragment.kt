package com.example.musicplayer.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.playerListener.PlayerListener
import com.example.musicplayer.playerListener.SeekBarListener
import com.example.musicplayer.utils.readableTime
import com.example.musicplayer.viewModel.Injection
import com.example.musicplayer.viewModel.SongViewModel
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects

class PlayerFragment : Fragment() {
    private lateinit var viewModel: SongViewModel
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            Injection.provideSongViewModelFactory(requireActivity())
        ).get(SongViewModel::class.java)
        bindBtn()
        bindPlayer()
        binding.seekBar.setOnSeekBarChangeListener(SeekBarListener(binding, viewModel.player))
        viewModel.player.addListener(PlayerListener(binding, viewModel))
    }


    private fun bindPlayer() {
        binding.apply {
            bindShuffle()
            progressTxt.text = readableTime(viewModel.player.currentPosition)
            progressDuration.text = readableTime(viewModel.player.duration)
            seekBar.progress = viewModel.player.currentPosition.toInt()
            seekBar.max = viewModel.player.duration.toInt()
            titlePlayer.text = viewModel.player.currentMediaItem?.mediaMetadata?.title
            titleArtist.text = viewModel.player.currentMediaItem?.mediaMetadata?.artist
            if (viewModel.player.isPlaying) {
                playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
            } else {
                playPauseBtn.setIconResource(R.drawable.ic_play_circle)
            }
            albumPhotoPlayer.load(Objects.requireNonNull(viewModel.player.currentMediaItem?.mediaMetadata?.artworkUri)) {
                placeholder(R.drawable.baseline_music_note_24)
                error(R.drawable.baseline_music_note_24)
            }
            lifecycleScope.launch {
                repeat(viewModel.player.duration.toInt()) {
                    if (viewModel.player.isPlaying) {
                        progressTxt.text = readableTime(viewModel.player.currentPosition)
                        seekBar.progress = viewModel.player.currentPosition.toInt()
                    }
                    delay(1000)
                }
            }
            shuffle.setOnClickListener {
                shuffle()
            }
            titlePlayer.isSelected = true
            titleArtist.isSelected = true
        }
    }

    private fun FragmentPlayerBinding.bindShuffle() {
        if(viewModel.player.repeatMode == ExoPlayer.REPEAT_MODE_ONE){
            viewModel.player.shuffleModeEnabled = false
            shuffle.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_repeat_one_24,
                0, 0, 0
            )}

        if(viewModel.player.repeatMode == ExoPlayer.REPEAT_MODE_ALL && !viewModel.player.shuffleModeEnabled) {
            shuffle.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_repeat_24,
                0, 0, 0
            )
        }
        if(viewModel.player.repeatMode == ExoPlayer.REPEAT_MODE_ALL && viewModel.player.shuffleModeEnabled){
            shuffle.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_shuffle_24,
                0, 0, 0
            )
        }
    }

    private fun FragmentPlayerBinding.shuffle() {
        when (viewModel.repeatMode) {
            0 -> {
                viewModel.player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                viewModel.player.shuffleModeEnabled = false
                viewModel.setRepeatMode(1)
                shuffle.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_repeat_one_24,
                    0, 0, 0
                )
            }

            1 -> {
                viewModel.player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                viewModel.player.shuffleModeEnabled = false
                viewModel.setRepeatMode(2)
                shuffle.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_repeat_24,
                    0, 0, 0
                )
            }

            2 -> {
                viewModel.player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                viewModel.player.shuffleModeEnabled = true
                viewModel.setRepeatMode(0)
                shuffle.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_shuffle_24,
                    0, 0, 0
                )
            }
        }
    }

    private fun bindBtn() {
        binding.back.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.playlist.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.nxtBtn.setOnClickListener {
            if (viewModel.player.hasNextMediaItem()) {
                viewModel.player.seekToNext()
            }
        }
        binding.backBtn.setOnClickListener {
            if (viewModel.player.hasPreviousMediaItem()) {
                viewModel.player.seekToPrevious()
            }
        }
        binding.playPauseBtn.setOnClickListener {
            if (viewModel.player.isPlaying) {
                viewModel.player.pause()
                binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
            } else {
                viewModel.player.play()
                binding.playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}