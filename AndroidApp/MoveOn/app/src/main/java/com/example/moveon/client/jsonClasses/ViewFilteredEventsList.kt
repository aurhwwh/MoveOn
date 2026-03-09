package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable


@Serializable
data class ViewFilteredEventsListRequest(
    val title: String? = null,
    val city: String? = null,
    val sportType: String? = null,
    val date: String? = null,
    val maxAmountOfPeople: Int? = null,
    val creatorRating: Double? = null
)

@Serializable
data class ViewFilteredEventsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventData>? = null
)