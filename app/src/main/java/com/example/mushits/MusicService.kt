package com.example.mushits

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.DefaultMediaNotificationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MusicService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var session: MediaSession
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()
        session = MediaSession.Builder(this, player).build()

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(this)
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return session
    }

    private fun saveLastPosition() {
        player.currentMediaItem?.mediaId?.toLongOrNull()?.let { songId ->
            val position = player.currentPosition
            if (player.duration > 0) {
                serviceScope.launch {
                    saveLastSong(applicationContext, songId, position)
                }
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        saveLastPosition()
        session.release()
        player.release()
        stopSelf()

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        saveLastPosition()
        serviceScope.cancel()
        session.release()
        player.release()
        super.onDestroy()
    }
}
