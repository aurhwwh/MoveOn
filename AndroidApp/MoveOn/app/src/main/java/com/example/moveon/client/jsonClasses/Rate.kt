package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable


@Serializable
data class UserRating(
    val ratedUserId: Int,
    val rating: Double
)


@Serializable
data class RateRequest(
    val eventId: Int,
    val ratings: List<UserRating>
)

@Serializable
data class RateResponse(
    val success: Boolean,
    val errorMessage: String? = null
)