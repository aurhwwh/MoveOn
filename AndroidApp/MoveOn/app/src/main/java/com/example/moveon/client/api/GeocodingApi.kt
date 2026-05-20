package com.example.moveon.client.api

import android.R
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.headers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GeocodingApi(private val client : HttpClient) {

    suspend fun searchPlace(query : String) : List<SearchResponse> {
        return client.get("https://nominatim.openstreetmap.org/search") {
            parameter("q", query)
            parameter("format", "json")
            parameter("addressdetails", 1)
            parameter("limit", 5)            // 10 by default
            parameter("countrycodes", "ru")
            parameter("bounded", 1)

            headers {
                append(
                    "User-Agent",
                    "MoveOnApp"
                )
            }
        }.body()
    }
}

@Serializable
data class Address(
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val suburb: String? = null,
    val road: String? = null,
    @SerialName("house_number")
    val houseNumber: String? = null
)

@Serializable
data class SearchResponse(
    @SerialName("display_name")
    val displayName: String,
    val lat: String,
    val lon: String,
    val address: Address? = null
)



