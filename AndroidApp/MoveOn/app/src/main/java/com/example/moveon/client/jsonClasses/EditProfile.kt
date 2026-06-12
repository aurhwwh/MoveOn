package com.example.moveon.client.jsonClasses

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class EditProfileRequest(
    val userName: String,
    val userSurname: String,
    val dateOfBirth: LocalDate,
    val description: String?
)

@Serializable
data class EditProfileResponse(
    val success: Boolean,
    val errorMessage: String? = null
)
