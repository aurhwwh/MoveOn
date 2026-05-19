package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate


@Serializable
data class RegisterRequest(
    val userName: String,
    val userSurname: String,
    val dateOfBirth: LocalDate,
    val email: String,
    val password: String,
    val gender: String
)


@Serializable
data class RegisterResponse(
    val success: Boolean,
    val errorMessage: String? = null
)