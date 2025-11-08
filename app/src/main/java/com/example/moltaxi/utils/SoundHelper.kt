package com.example.moltaxi.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.moltaxi.R

class SoundHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playStartSound() {
        playSound(R.raw.sound_start)
    }

    fun playStopSound() {
        playSound(R.raw.sound_stop)
    }

    fun playClickSound() {
        playSound(R.raw.sound_click)
    }

    private fun playSound(resourceId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resourceId)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}