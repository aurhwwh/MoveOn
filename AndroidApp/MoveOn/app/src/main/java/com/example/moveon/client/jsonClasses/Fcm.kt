package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class StoreFcmTokenRequest(
    val token: String
)
@Serializable
data class StoreFcmTokenResponse(
    val success: Boolean,
    val errorMessage: String? = null
)