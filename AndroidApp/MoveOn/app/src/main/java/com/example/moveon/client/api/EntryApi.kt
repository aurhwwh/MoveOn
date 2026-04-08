package com.example.moveon.client.api

import com.example.moveon.client.jsonClasses.RegisterRequest
import com.example.moveon.client.jsonClasses.RegisterResponse
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
}