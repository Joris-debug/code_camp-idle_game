package com.example.idle_game.api

import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cookies = response.headers("Set-Cookie")
        if (cookies.isNotEmpty()) {
            for (cookie in cookies) {
                println("Cookie: $cookie")
            }
        }
        return response
    }
}
