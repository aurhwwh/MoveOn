package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class EventData(
    val eventId: Int,
    val photoId: Int?,
    val title: String,
    val description: String,
    val city: String,
    val sportType: String,
    val date: String,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int,
    val creatorRating: Double,
)