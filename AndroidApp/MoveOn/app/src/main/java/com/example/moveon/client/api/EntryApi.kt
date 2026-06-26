package com.example.moveon.client.api

import com.example.moveon.client.jsonClasses.LoginRequest
import com.example.moveon.client.jsonClasses.LoginResponse
import com.example.moveon.client.jsonClasses.RefreshRequest
import com.example.moveon.client.jsonClasses.RefreshResponse
import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.client.jsonClasses.RegisterResponse
import com.example.moveon.client.jsonClasses.StoreFcmTokenRequest
import com.example.moveon.client.jsonClasses.StoreFcmTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EntryApi (val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun register(request : RegisterRequest) : RegisterResponse {
        return client.post("$baseUrl/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun login(request : LoginRequest) : LoginResponse {
        return client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun storeFcmToken(request : StoreFcmTokenRequest) : StoreFcmTokenResponse{
        return client.post("$baseUrl/store_fcm_token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}