package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
data class ViewMyEventsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventListElement>? = null
)
