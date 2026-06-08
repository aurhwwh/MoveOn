package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
data class ViewEventsMarkersResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventsMarker>? = null
)

@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class EventsMarker (
    val eventId: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val sportType: String,
    val dateTime: Instant,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int
)
