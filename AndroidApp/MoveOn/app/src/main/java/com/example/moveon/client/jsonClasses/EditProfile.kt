package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class EditProfileRequest(
    val userName: String,
    val userSurname: String,
    val dateOfBirth: LocalDate,
    val description: String?,
    val photoId: Int? = null
)

@Serializable
data class EditProfileResponse(
    val success: Boolean,
    val errorMessage: String? = null
)