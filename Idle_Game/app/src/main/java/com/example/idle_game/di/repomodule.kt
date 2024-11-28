package com.example.idle_game.di

import android.content.Context
import androidx.room.Room
import com.example.idle_game.data.database.GameDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object repomodule {

    @Provides
    fun providesDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context, GameDatabase::class.java, "gamedatabase"
        ).build()
    }

}