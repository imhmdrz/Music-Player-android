package com.example.musicplayer.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapter.RvAdapter
import com.example.musicplayer.dataStore.dataStore
import com.example.musicplayer.databinding.FragmentMainBinding
import com.example.musicplayer.playerListener.MainListener
import com.example.musicplayer.service.PlayerService
import com.example.musicplayer.viewModel.Injection
import com.example.musicplayer.viewModel.SongViewModel
import com.google.android.exoplayer2.ExoPlayer
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var binder: PlayerService.PlayerServiceBinder? = null
    private lateinit var viewModel: SongViewModel
    private lateinit var rvAdapter: RvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            checkReadStoragePermissions()
        }else{
            checkReadStoragePermissionsForOlderVersions()
        }
        viewModel = ViewModelProvider(
            requireActivity(),
            Injection.provideSongViewModelFactory(requireActivity(), requireActivity().dataStore)
        ).get(SongViewModel::class.java)
//        if(PlayerService.isStarted){
//            if (viewModel.player.isPlaying) {
//                binding.playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
//            } else {
//                binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
//            }
//        }
        bindService()
        bindRV()
        bindNavigation()
        bindBTN()
    }

    private fun bindNavigation() {
        binding.uPBtn.setOnClickListener {
            if (PlayerService.isStarted) {
                findNavController().navigate(R.id.action_mainFragment_to_playerFragment)
            } else {
                Toast.makeText(requireContext(), "No song is playing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS),
                2
            )
        }
    }
    private fun checkReadStoragePermissionsForOlderVersions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
    }
    private fun bindService() {
        lifecycleScope.launch {
            viewModel.shouldBeClear.collect{
                if(it){
                    requireActivity().unbindService(playerServiceConnection)
                    val intent = Intent(requireActivity(), PlayerService::class.java)
                    requireActivity().stopService(intent)
                }
            }
        }
        val intent = Intent(requireActivity(), PlayerService::class.java)
        requireActivity().bindService(intent, playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as PlayerService.PlayerServiceBinder
            viewModel.player = binder!!.getService().currentPlayer as ExoPlayer
            viewModel.player.addListener(MainListener(binding, viewModel))
            lifecycleScope.launch {
                if (viewModel.player.isPlaying) {
                    binding.playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
                } else {
                    binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }


    private fun bindBTN() {
        binding.prevBtn.setOnClickListener {
            if (viewModel.player.hasPreviousMediaItem()) {
                viewModel.player.seekToPrevious()
                if (viewModel.player.isPlaying) {
                    binding.playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
                } else {
                    binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
                }
            }
        }
        binding.nextBtn.setOnClickListener {
            if (viewModel.player.hasNextMediaItem()) {
                viewModel.player.seekToNext()
                if (viewModel.player.isPlaying) {
                    binding.playPauseBtn.setIconResource(R.drawable.ic_pause_circle)
                } else {
                    binding.playPauseBtn.setIconResource(R.drawable.ic_play_circle)
                }
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


    private fun bindRV() {
        rvAdapter = RvAdapter(requireActivity(), viewModel)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.setHasFixedSize(true)
        val animatorAdapter = ScaleInAnimationAdapter(rvAdapter)
        animatorAdapter.setDuration(500)
        animatorAdapter.setInterpolator(LinearOutSlowInInterpolator())
        animatorAdapter.setFirstOnly(false)
        binding.recyclerview.adapter = animatorAdapter
        lifecycleScope.launch {
            viewModel.song.collect {
                rvAdapter.submitList(it)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        if(binder != null){
            doUnbindService()
        }
        super.onDestroy()
    }

    private fun doUnbindService() {
        requireActivity().unbindService(playerServiceConnection)
    }

}