package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class CreateEventRequest (
    val title: String,
    val description: String,
    val dateTime: Instant,
    val maxAmountOfPeople: Int,
    val sportType: String,
    val city: String = "Unknown",
    val place: String,
    val lat: Double,
    val lon: Double
)


@Serializable
data class CreateEventResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val eventId: Int? = null
)