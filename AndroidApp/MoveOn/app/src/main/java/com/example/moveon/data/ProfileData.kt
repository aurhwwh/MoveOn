package com.example.moveon.data

import kotlinx.datetime.LocalDate

class ProfileData (
    val photoId: Int,
    val name: String,
    val surname: String,
    val birth: LocalDate,
    val city: String,
    val description: String,
    val rating: Double
)