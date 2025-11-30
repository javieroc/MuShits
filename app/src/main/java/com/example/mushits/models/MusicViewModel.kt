package com.example.mushits.models

import android.app.Application
import android.content.ComponentName
import android.content.ContentUris
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mushits.data.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mushits.MusicService
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import kotlinx.coroutines.delay

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application

    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller: StateFlow<MediaController?> = _controller

    fun connectToService() {
        val token = SessionToken(context, ComponentName(context, MusicService::class.java))

        val controllerFuture = MediaController.Builder(context, token).buildAsync()

        Futures.addCallback(controllerFuture,
            object : FutureCallback<MediaController> {
                override fun onSuccess(result: MediaController) {
                    _controller.value = result
                    observePlayer(result)

                    if (_songs.value.isNotEmpty()) {
                        setPlaylist()
                    }
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun observePlayer(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                val song = _songs.value.find { it.uri == item?.localConfiguration?.uri.toString() }
                _currentSong.value = song
            }

            override fun onPlaybackStateChanged(state: Int) {
                viewModelScope.launch {
                    while (controller.isPlaying) {
                        _position.value = controller.currentPosition
                        delay(100)
                    }
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                _position.value = controller.currentPosition
            }
        })
    }

    private val contentResolver = application.contentResolver

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _position = MutableStateFlow(0L)
    val position: StateFlow<Long> = _position

    fun loadSongs() {
        viewModelScope.launch {
            val list = queryDeviceSongs()
            _songs.value = list

            controller.value?.let {
                if (list.isNotEmpty()) setPlaylist()
            }
        }
    }

    private fun queryDeviceSongs(): List<Song> {
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val songList = mutableListOf<Song>()

        contentResolver.query(
            audioUri,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val artist = cursor.getString(artistCol)
                val duration = cursor.getLong(durationCol)
                val data = cursor.getString(dataCol)
                val albumId = cursor.getLong(albumIdCol)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val artUri: String? =
                    if (albumId != 0L)
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            albumId
                        ).toString()
                    else null

                songList.add(
                    Song(id, title, artist, duration, data, uri = contentUri.toString(), artUri)
                )
            }
        }

        return songList
    }

    fun playSong(song: Song) {
        val c = controller.value ?: return
        val index = songs.value.indexOfFirst { it.id == song.id }
        if (index != -1) {
            c.seekTo(index, 0L)
            c.play()
            _currentSong.value = song
        }
    }

    fun togglePlayPause() {
        val c = controller.value ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun seekTo(ms: Long) {
        controller.value?.seekTo(ms)
    }

    fun playNext() {
        val list = songs.value
        val current = currentSong.value ?: return

        val index = list.indexOfFirst { it.id == current.id }
        if (index == -1 || index == list.lastIndex) return

        playSong(list[index + 1])
    }

    fun playPrevious() {
        val c = controller.value ?: return
        val list = songs.value
        val current = currentSong.value ?: return
        val thresholdMs = 5000L

        if (c.currentPosition > thresholdMs) {
            seekTo(0)
            return
        }

        val index = list.indexOfFirst { it.id == current.id }
        if (index <= 0) {
            seekTo(0)
            return
        }

        playSong(list[index - 1])
    }

    fun setPlaylist() {
        val c = controller.value ?: return
        val s = songs.value
        if (s.isEmpty()) return

        val items = s.map { MediaItem.fromUri(it.uri) }
        c.setMediaItems(items)
        c.prepare()
    }
}
