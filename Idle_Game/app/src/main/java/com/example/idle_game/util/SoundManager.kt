package com.example.idle_game.util

import android.content.Context
import android.media.SoundPool
import com.example.idle_game.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    private val soundPool: SoundPool
) {
    private val soundResourceIds = mutableMapOf<Int, Int>()

    companion object {
        val BUTTON_CLICK_SOUND_RESOURCE_ID = R.raw.cancel
        val CURSOR_SOUND_RESOURCE_ID = R.raw.cursor
    }

    fun initializeSoundEffects(context: Context) {
        loadSound(context, CURSOR_SOUND_RESOURCE_ID)
        loadSound(context, BUTTON_CLICK_SOUND_RESOURCE_ID)
    }

    private fun loadSound(context: Context, soundResourceId: Int) {
        if (!soundResourceIds.containsKey(soundResourceId)) {
            val soundId = soundPool.load(context, soundResourceId, 1)
            soundResourceIds[soundResourceId] = soundId
        }
    }

    fun playSound(soundResourceId: Int) {
        val soundId = soundResourceIds[soundResourceId]
        if (soundId != null && soundId != -1) {
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

}
