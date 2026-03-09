package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class ViewProfile(
    val success: Boolean,
    val errorMessage: String? = null,
    val photoId: Int? = null, //temporary
    val userName: String? = null,
    val userSurname: String? = null,
    val dateOfBirth: String? = null,
    val city: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    val friendsAmount: Int? = null
)