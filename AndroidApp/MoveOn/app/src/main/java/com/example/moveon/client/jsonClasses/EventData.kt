package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class EventData(
    val eventId: Int,
    val title: String,
    val city: String,
    val sportType: String,
    val dateTime: Instant,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int,
    val creatorRating: Double,
    val photoId: Int,
    val description: String
)