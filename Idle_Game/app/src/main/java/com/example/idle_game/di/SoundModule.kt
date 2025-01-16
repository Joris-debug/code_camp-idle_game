package com.example.idle_game.di

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.idle_game.util.SoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SoundModule {

    @Provides
    fun provideSoundPool(): SoundPool {
        return SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    @Provides
    fun providesSoundManager(
        @ApplicationContext context: Context,
        soundPool: SoundPool
    ): SoundManager {
        val soundManager = SoundManager(soundPool)
        soundManager.initializeSoundEffects(context)
        return soundManager
    }

    companion object {
        const val MAX_STREAMS = 16
    }
}