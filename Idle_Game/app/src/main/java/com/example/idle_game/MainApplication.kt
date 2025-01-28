package com.example.idle_game

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    private val customWorkManagerConfiguration = Configuration.Builder()
        .setWorkerFactory(EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory())
        .build()

    override fun onCreate() {
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return customWorkManagerConfiguration
    }
}