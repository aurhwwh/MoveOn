package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlin.time.Instant


@Serializable
@OptIn(kotlin.time.ExperimentalTime::class)
data class ViewEventResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val creatorId: Int? = null,
    val participants: List<Person>? = null,
    val title: String? = null,
    val description: String? = null,
    val dateTime: Instant? = null,
    val currentAmountOfPeople: Int? = null,
    val maxAmountOfPeople: Int? = null,
    val sportType: String? = null,
    val isUserParticipant: Boolean? = null,
    val isUserCreator: Boolean? = null,
    val photoId: Int? = null
)


@Serializable
data class Person(
    val id: Int,
    val name: String,
    val surname: String,
    val rating: Double? = null,
    val photoId: Int? = null
)