package com.example.idle_game.api

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())

        val cookies = response.headers("Set-Cookie")
        for (cookie in cookies) {
            val cookieParts = cookie.split(";") // So I can ignore meta-data
            if (cookieParts.isEmpty()) {
                continue // Cookie does not have the format I am looking for
            }
            if(cookieParts.first().startsWith("refresh_token=")) {
                val tokenValue = cookieParts.first()
                sharedPreferences.edit().putString("refresh_token", tokenValue).apply()
            }
        }

        val authorization = response.headers("Authorization")
        if (authorization.size == 1) { // I only expect a single argument
            val tokenValue = authorization.first()
            sharedPreferences.edit().putString("access_token", tokenValue).apply()
        }

        return response
    }

}
