package com.example.musicplayer.service


import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.utils.uriToBitmap
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil.IMPORTANCE_HIGH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PlayerService : Service() {
    companion object {
        var isStarted = false
        private const val channelId = "Music Channel"
        private const val notificationId = 111
    }
    lateinit var currentPlayer: Player
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private val binder: IBinder = PlayerServiceBinder()
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var isForegroundService = false

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        currentPlayer = ExoPlayer.Builder(applicationContext).build()
        (currentPlayer as ExoPlayer).setAudioAttributes(audioAttributes, true)
        (currentPlayer as ExoPlayer).setHandleAudioBecomingNoisy(true)
        playerNotificationManager = playerNotificationManager()
        playerNotificationManager.apply {
            setPlayer(currentPlayer)
            setPriority(NotificationCompat.PRIORITY_MAX)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
            setColorized(false)
        }
    }

    private fun playerNotificationManager() =
        PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setMediaDescriptionAdapter(adapter)
            .setNotificationListener(PlayerNotificationListener())
            .setChannelImportance(IMPORTANCE_HIGH)
            .setSmallIconResourceId(R.drawable.baseline_music_note_24)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setChannelNameResourceId(R.string.app_name)
            .build()

    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int, notification: Notification, ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext, Intent(applicationContext, this@PlayerService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(STOP_FOREGROUND_DETACH)
            if (currentPlayer.isPlaying) {
                currentPlayer.pause()
            }
            isForegroundService = false
            stopSelf()
        }
    }
    private val adapter: PlayerNotificationManager.MediaDescriptionAdapter =
        object : PlayerNotificationManager.MediaDescriptionAdapter {
            var currentIconUri: Uri? = null
            var currentBitmap: Bitmap? = null
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return player.currentMediaItem?.mediaMetadata?.title.toString()
            }
            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val openAppIntent = Intent(applicationContext, MainActivity::class.java)
                return PendingIntent.getActivity(
                    applicationContext, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE
                )
            }
            override fun getCurrentContentText(player: Player): CharSequence {
                return player.currentMediaItem?.mediaMetadata?.artist.toString()
            }

            override fun getCurrentLargeIcon(
                player: Player, callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                val iconUri = player.currentMediaItem?.mediaMetadata?.artworkUri
                return if (currentIconUri != iconUri || currentBitmap == null) {
                    currentIconUri = iconUri
                    serviceScope.launch {
                        currentBitmap = iconUri?.let {
                            uriToBitmap(contentResolver, it)
                        }
                        currentBitmap?.let { callback.onBitmap(it) }
                    }
                    null
                } else {
                    currentBitmap
                }
            }
        }
    override fun onDestroy() {
        if (currentPlayer.isPlaying) {
            currentPlayer.stop()
        }
        playerNotificationManager.setPlayer(null)
        currentPlayer.release()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        isStarted = false
        super.onDestroy()
    }
}