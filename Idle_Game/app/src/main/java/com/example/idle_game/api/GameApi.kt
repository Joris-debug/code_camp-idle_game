package com.example.idle_game.api

import com.example.idle_game.api.models.SignUpRequest
import com.example.idle_game.api.models.ServerResponse
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
    suspend fun signUp(@Body signUpRequest: SignUpRequest): ServerResponse

    @GET("/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Header("Cookie") refreshToken: String): ServerResponse
}