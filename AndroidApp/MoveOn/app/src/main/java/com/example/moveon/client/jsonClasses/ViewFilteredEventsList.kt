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
    val events: List<EventData>? = null
)