package com.example.idle_game.api

import com.example.idle_game.api.models.SignUpRequest
import com.example.idle_game.api.models.SignUpResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

    companion object {
        const val BASE_URL = "https://codecamp.comtec.eecs.uni-kassel.de/"
    }

    @POST("/sign-up")
    @Headers("Content-Type: application/json")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): SignUpResponse

}