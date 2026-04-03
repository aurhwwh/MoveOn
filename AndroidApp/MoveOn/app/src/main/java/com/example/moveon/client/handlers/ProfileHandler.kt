package com.example.moveon.client.handlers
import com.example.moveon.client.api.ProfileApi
import com.example.moveon.data.ProfileData

class ProfileHandler (private val api : ProfileApi) {

    suspend fun getProfile(userId : Int) : ProfileData {
        val response = api.getProfile(userId);

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return ProfileData(
            photoId = response.photoId ?: -1,
            name = response.userName ?: "",
            surname = response.userSurname ?: "",
            birth = response.dateOfBirth ?: throw Exception("Date of birth is missing"),
            city = response.city ?: "",
            description = response.description ?: "",
            rating = response.rating ?: 0.0
        )
    }
}