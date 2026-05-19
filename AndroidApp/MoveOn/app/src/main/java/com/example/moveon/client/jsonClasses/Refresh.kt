package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val oldRefreshToken: String
)
@Serializable
data class RefreshResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val newRefreshToken: String? = null,
    val newAccessToken: String? = null
)