package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable


@Serializable
data class ViewEventsMarkersResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventsMarker>? = null
)

@Serializable
data class EventsMarker (
    val eventId: Int,
    val title: String,
    val lat: Double,
    val lon: Double
)
