package com.example.moveon.client.handlers

import com.example.moveon.client.api.GeocodingApi
import com.example.moveon.client.api.SearchResponse

class GeocodingHandler(private val api : GeocodingApi) {

    suspend fun searchPlace(query: String) : List<Place> {
        val response = api.searchPlace(query)

        return response
            .map { it.toPlace() }
            .filter {
                it.city == "Москва" || it.city == "Санкт-Петербург"
            }
    }
}


data class Place (
    val name: String,
    val city: String,
    val lat: Double,
    val lon: Double
)


fun SearchResponse.toPlace() : Place {
    val city = address?.city ?: address?.town ?: address?.village ?: "Unknown"

    val street = listOfNotNull(
        address?.road,
        address?.houseNumber
    ).joinToString(" ")

    val display = if (street.isNotBlank()) {
        "$street, $city"
    } else {
        city
    }

    return Place(
        name = display,
        city = city,
        lat = lat.toDouble(),
        lon = lon.toDouble()
    )
}
