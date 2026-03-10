package MoveOn

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val errorMessage: String? = null
)

@Serializable
data class RegisterRequest(
    val userName: String,
    val userSurname: String,
    val dateOfBirth: String,
    val email: String,
    val password: String,
    val gender: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val userId: Int? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class ViewProfileResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val photoId: Int? = null,
    val userName: String? = null,
    val userSurname: String? = null,
    val dateOfBirth: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    val friendsAmount: Int? = null
)

@Serializable
data class CreateEventResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val eventId: Int? = null
)

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String,
    val time: String,
    val date: String,
    val maxAmountOfPeople: Int,
    val sportType: String,
    val creatorId: Int,
)

@Serializable
data class EventListElement(
    val eventId: Int?,
    val title: String,
    val city: String,
    val sportType: String,
    val date: String,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int,
    val creatorRating: Double,
)

@Serializable
data class ViewFilteredEventsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventListElement>? = null
)

@Serializable
data class ViewFilteredEventsListRequest(
    val title: String,
    val city: String,
    val sportType: String,
    val date: String,
    val maxAmountOfPeople: Int,
    val creatorRating: Double
)

@Serializable
data class ViewEventResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val creatorId: Int? = null,
    val participantIds: List<Int>? = null,
    val title: String? = null,
    val description: String? = null,
    val time: String? = null,
    val date: String? = null,
    val currentAmountOfPeople: Int? = null,
    val maxAmountOfPeople: Int? = null,
    val sportType: String? = null
)

@Serializable
data class JoinApplicationResponse(
    val success: Boolean,
    val errorMessage: String? = null
)

@Serializable
data class JoinApplicationRequest(
    val eventId: Int,
    val userId: Int
)

@Serializable
data class Notification(
    val eventId: Int? = null,
    val otherUserId: Int? = null,
)

@Serializable
data class OpenNotificationsResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val notifications: List<Notification>? = null
)

@Serializable
data class EventApplication(
    val eventId: Int,
    val title: String,
    val date: String,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int
)

@Serializable
data class OpenApplicationListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val eventApplications: List<EventApplication>? = null
)

@Serializable
data class Person(
    val id: Int,
    val name: String,
    val surname: String
)

@Serializable
data class GetPersonsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val persons: List<Person>? = null
)

@Serializable
data class RateRequest(
    val userWhoRatesId: Int,
    val ratedUserId: Int,
    val rating: Double,
    val eventId: Int? = null,
)

@Serializable
data class RateResponse(
    val success: Boolean,
    val errorMessage: String? = null
)

@Serializable
data class AcceptOrDeclineEventApplicationResponse(
    val success: Boolean,
    val errorMessage: String? = null
)

@Serializable
data class AcceptOrDeclineEventApplicationRequest(
    val eventId: Int,
    val userId: Int,
    val isAccepted: Boolean
)