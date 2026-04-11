package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class JoinApplicationRequest(
    val eventId: Int
)


@Serializable
data class JoinApplicationResponse(
    val success: Boolean,
    val errorMessage: String? = null
)
