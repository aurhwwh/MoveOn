package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class ViewFilteredEventsListRequest(
    val title: String? = null,
    val city: String? = null,
    val sportType: String? = null,
    val dateTime: Instant? = null,
    val maxAmountOfPeople: Int? = null,
    val creatorRating: Double? = null
)


@Serializable
data class ViewFilteredEventsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventListElement>? = null
)


@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class EventListElement(
    val eventId: Int,
    val title: String,
    val city: String,
    val sportType: String,
    val dateTime: Instant,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int,
    val creatorRating: Double,
    val photoId: Int,
    val description: String,
    val isCreator: Boolean = false
)