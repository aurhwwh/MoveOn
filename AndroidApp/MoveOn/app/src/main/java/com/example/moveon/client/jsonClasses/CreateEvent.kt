package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class CreateEventRequest (
    val title: String,
    val description: String,
    val dateTime: Instant,
    //val position: Position, пока непонятно в каком формате, есть какие-то встроенные
    val maxAmountOfPeople: Int,
    val sportType: String
)


@Serializable
data class CreateEventResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val eventId: Int? = null
)