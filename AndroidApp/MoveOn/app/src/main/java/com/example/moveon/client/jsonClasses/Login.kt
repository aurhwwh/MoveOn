package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)