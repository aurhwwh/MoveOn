package com.example.moveon.client.handlers

import com.example.moveon.client.api.EntryApi
import com.example.moveon.client.jsonClasses.LoginRequest
import com.example.moveon.client.jsonClasses.LoginResponse
import com.example.moveon.client.jsonClasses.RefreshRequest
import com.example.moveon.client.jsonClasses.RefreshResponse
import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.client.jsonClasses.RegisterResponse

class EntryHandler(private val api : EntryApi) {

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return api.register(request)
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return api.login(request)
    }

    suspend fun refresh(request: RefreshRequest): RefreshResponse {
        return api.refresh(request)
    }
}