package com.example.moveon.client.handlers

import com.example.moveon.client.api.GeocodingApi
import com.example.moveon.client.api.ReverseResponse
import com.example.moveon.client.api.SearchResponse


class GeocodingHandler(private val api : GeocodingApi) {

    suspend fun searchPlace(query: String) : List<Place> {
        val response = api.searchPlace(query)

        return response
            .map { it.toPlace() }
            .filter {
                it.city == "Москва" || it.city == "Санкт-Петербург" || it.city == "Moscow" || it.city == "Saint Petersburg"
            }
    }

    suspend fun reverseGeocode(lat : Double, lon : Double) : Place {
        return try {
            val response = api.reverseGeocode(lat, lon)
            response.toPlace()
        } catch (e : Exception) {
            Place (
                name = "%.6f, %.6f".format(lat, lon),
                city = "",
                lat = lat,
                lon = lon
            )
        }
    }
}


data class Place (
    val name: String,
    val city: String,
    val lat: Double,
    val lon: Double
)


private fun buildPlace(
    address: com.example.moveon.client.api.Address?,
    lat: String,
    lon: String
): Place {

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


fun SearchResponse.toPlace() : Place = buildPlace(address, lat, lon)

fun ReverseResponse.toPlace() : Place = buildPlace(address, lat, lon)
