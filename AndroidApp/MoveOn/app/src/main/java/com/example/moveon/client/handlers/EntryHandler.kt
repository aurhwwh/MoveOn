package com.example.moveon.client.handlers

import com.example.moveon.client.api.EntryApi
import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.client.jsonClasses.RegisterResponse

class EntryHandler(private val api : EntryApi) {

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return api.register(request)
    }
}