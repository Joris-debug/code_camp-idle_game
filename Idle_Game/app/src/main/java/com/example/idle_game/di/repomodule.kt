package com.example.idle_game.di

import android.content.Context
import androidx.room.Room
import com.example.idle_game.api.GameApi
import com.example.idle_game.data.database.GameDatabase
import com.example.idle_game.data.repositories.GameRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object repomodule {

    data class RetroFitHolder(val gameRetrofit: Retrofit, val quotesRetrofit: Retrofit)

    @Singleton
    @Provides
    fun providesGameRepository(api: GameApi, gameDb: GameDatabase): GameRepository {
        return GameRepository(api, gameDb.gameDao)
    }

    @Provides
    fun providesMoshi(): Moshi {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return moshi
    }

    @Provides
    fun providesAPI(moshi: Moshi): GameApi {

        val retrofit = Retrofit.Builder()
            .baseUrl(GameApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(GameApi::class.java)
    }

    @Provides
    fun providesDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context, GameDatabase::class.java, "gamedatabase"
        ).build()
    }
}