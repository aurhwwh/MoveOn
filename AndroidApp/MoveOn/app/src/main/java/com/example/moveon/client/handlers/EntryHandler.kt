package com.example.moveon.client.handlers

import com.example.moveon.client.api.EntryApi
import com.example.moveon.client.jsonClasses.LoginRequest
import com.example.moveon.client.jsonClasses.LoginResponse
import com.example.moveon.client.jsonClasses.RefreshRequest
import com.example.moveon.client.jsonClasses.RefreshResponse
import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.client.jsonClasses.RegisterResponse
import com.example.moveon.client.jsonClasses.StoreFcmTokenRequest
import com.example.moveon.client.jsonClasses.StoreFcmTokenResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EntryHandler(private val api : EntryApi) {

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return api.register(request)
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return api.login(request)
    }

    suspend fun storeFcmToken(request : StoreFcmTokenRequest) : StoreFcmTokenResponse{
        return api.storeFcmToken(request)
    }
}