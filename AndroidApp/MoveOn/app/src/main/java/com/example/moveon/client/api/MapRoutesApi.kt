package com.example.moveon.client.api

import com.example.moveon.client.jsonClasses.RouteOptionsResponse
import com.example.moveon.client.jsonClasses.ViewProfile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MapRoutesApi(private val client: HttpClient) {
    private val baseUrl = "http://46.243.211.49:8080/"

    suspend fun getRouteOptions(lat: Double, lon : Double, radius: Int): RouteOptionsResponse {
        return client.get("$baseUrl/route_options") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("radius", radius)
        }.body()
    }
}