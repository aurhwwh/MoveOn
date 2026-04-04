package com.example.moveon.client.jsonClasses

import com.example.moveon.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ViewProfile(
    val success: Boolean,
    val errorMessage: String? = null,
    val photoId: Int? = null, //temporary
    val userName: String? = null,
    val userSurname: String? = null,
    val dateOfBirth: LocalDate? = null,
    val city: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    val friendsAmount: Int? = null
)