package com.example.moveon.client.api

import com.example.moveon.client.jsonClasses.ViewProfileResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ProfileApi(private val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getProfile(userId: Int): ViewProfileResponse {
        return client.get("$baseUrl/view_user_profile") {
            parameter("userId", userId)
        }.body()
    }
}