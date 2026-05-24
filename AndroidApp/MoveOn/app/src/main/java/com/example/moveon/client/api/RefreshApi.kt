package com.example.moveon.client.api

import com.example.moveon.client.jsonClasses.RefreshRequest
import com.example.moveon.client.jsonClasses.RefreshResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object RefreshApi {
    private val baseUrl = "http://10.0.2.2:8080"

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun refresh(request: RefreshRequest): RefreshResponse {
        return client.post("$baseUrl/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}