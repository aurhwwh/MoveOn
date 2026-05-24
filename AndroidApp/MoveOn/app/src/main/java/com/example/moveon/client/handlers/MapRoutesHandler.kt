package com.example.moveon.client.handlers

import com.example.moveon.client.api.MapRoutesApi
import com.example.moveon.client.api.ProfileApi
import com.example.moveon.client.jsonClasses.RouteOptionsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MapRoutesHandler(private val api : MapRoutesApi) {
    suspend fun getRouteOptions(lat: Double, lon : Double, radius: Int): RouteOptionsResponse {
        val response = api.getRouteOptions(lat,lon,radius)
        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }
        return response
    }

}