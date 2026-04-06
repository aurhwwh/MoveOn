package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Serializable
data class CreateEventRequest @OptIn(ExperimentalTime::class) constructor(
    val title: String,
    val description: String,
    val time: Instant,
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