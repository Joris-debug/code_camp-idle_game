package com.example.idle_game.api

import com.example.idle_game.api.models.ItemResponse
import com.example.idle_game.api.models.ScoreResponse
import com.example.idle_game.api.models.SetScoreRequest
import com.example.idle_game.api.models.ServerResponse
import com.example.idle_game.api.models.UserCredentialsRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface GameApi {

    companion object {
        const val BASE_URL = "https://codecamp.comtec.eecs.uni-kassel.de/"
    }

    @POST("/sign-up")
    @Headers("Content-Type: application/json")
    suspend fun signUp(@Body userCredentialsRequest: UserCredentialsRequest): ServerResponse

    @POST("/sign-in")
    @Headers("Content-Type: application/json")
    suspend fun signIn(@Body userCredentialsRequest: UserCredentialsRequest): ServerResponse

    @GET("/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Header("Cookie") refreshToken: String): ServerResponse

    @GET("/items")
    @Headers("Content-Type: application/json")
    suspend fun getItems(@Header("Authorization") accessToken: String): List<ItemResponse>

    @GET("/score")
    @Headers("Content-Type: application/json")
    suspend fun getScore(@Header("Authorization") accessToken: String): List<ScoreResponse>

    @POST("/score")
    suspend fun postScore(
        @Header("Authorization") accessToken: String,
        @Body setScoreRequest: SetScoreRequest
    ): ServerResponse


}