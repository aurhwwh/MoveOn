package com.example.moveon.data

import kotlinx.datetime.LocalDate

data class ProfileData (
    val photoId: Int? = null,
    val name: String,
    val surname: String,
    val birth: LocalDate,
    val city: String,
    val description: String,
    val rating: Double
)