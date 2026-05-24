package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val lat: Double,
    val lon: Double
)

@Serializable
data class Route(
    val points: List<Point>,
    val distance: Double,
    val time: Long
)

@Serializable
data class RouteOptionsResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val centralPoint: Point? = null,
    val points: List<Point>? = null,
    val routes: List<Route>? = null,
)