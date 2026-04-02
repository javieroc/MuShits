package com.connan.mushits.models

import android.app.Application
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import com.connan.mushits.R

class SoundViewModel(application: Application) : AndroidViewModel(application) {
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<Int, Int>()
    private var lastStreamId: Int = 0

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1) // Set to 1 to help prevent overlaps at the pool level too
            .setAudioAttributes(attrs)
            .build()

        soundMap[0] = soundPool.load(application, R.raw.fart, 1)
        soundMap[1] = soundPool.load(application, R.raw.fart2, 1)
        soundMap[2] = soundPool.load(application, R.raw.cat, 1)
        soundMap[3] = soundPool.load(application, R.raw.aliens, 1)
        soundMap[4] = soundPool.load(application, R.raw.alarm, 1)
        soundMap[5] = soundPool.load(application, R.raw.applause, 1)
        soundMap[6] = soundPool.load(application, R.raw.demon, 1)
        soundMap[7] = soundPool.load(application, R.raw.demon2, 1)
        soundMap[8] = soundPool.load(application, R.raw.ghost, 1)
        soundMap[9] = soundPool.load(application, R.raw.knock, 1)
        soundMap[10] = soundPool.load(application, R.raw.laughing, 1)
        soundMap[11] = soundPool.load(application, R.raw.shotgun, 1)
    }

    fun playSound(index: Int) {
        // Stop the previous sound if it's still playing
        if (lastStreamId != 0) {
            soundPool.stop(lastStreamId)
        }

        soundMap[index]?.let { id ->
            lastStreamId = soundPool.play(id, 1f, 1f, 0, 0, 1f)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
    }
}
