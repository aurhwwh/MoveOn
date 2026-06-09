package com.example.moveon.client.api

import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.CreateEventResponse
import com.example.moveon.client.jsonClasses.EditProfileRequest
import com.example.moveon.client.jsonClasses.EditProfileResponse
import com.example.moveon.client.jsonClasses.ViewProfile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ProfileApi(private val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getUserProfile(userId: Int): ViewProfile {
        return client.get("$baseUrl/view_user_profile") {
            parameter("userId", userId)
        }.body()
    }

    suspend fun getMyProfile() : ViewProfile {
        return client.get("$baseUrl/view_my_profile").body()
    }

    suspend fun editProfile(request : EditProfileRequest) : EditProfileResponse {
        return client.post("$baseUrl/edit_profile") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}