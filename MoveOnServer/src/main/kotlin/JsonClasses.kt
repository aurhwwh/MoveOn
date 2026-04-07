package MoveOn

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Serializable
data class RefreshRequest(
    val oldRefreshToken: String
)
@Serializable
data class RefreshResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val newRefreshToken: String? = null,
    val newAccessToken: String? = null
)

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val errorMessage: String? = null
)

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

@Serializable
data class ViewProfileResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val photoId: Int? = null, //temporary
    val userName: String? = null,
    val userSurname: String? = null,
    val dateOfBirth: LocalDate? = null,
    val description: String? = null,
    val rating: Double? = null,
    val friendsAmount: Int? = null
)

//@Serializable
//data class ViewProfileRequest(
//    val userId: Int
//)

@Serializable
data class CreateEventResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val eventId: Int? = null
)

@Serializable
data class CreateEventRequest @OptIn(ExperimentalTime::class) constructor(
    val title: String,
    val description: String,
    val dateTime: Instant,
    //val position: Position, пока непонятно в каком формате, есть какие-то встроенные
    val maxAmountOfPeople: Int,
    val sportType: String
)

@Serializable
data class EventListElement @OptIn(ExperimentalTime::class) constructor(
    val eventId: Int,
    val title: String,
    val city: String,
    val sportType: String,
    val dateTime: Instant?,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int,
    val creatorRating: Double,
    val photoId: Int,
    val description: String
)

@Serializable
data class ViewFilteredEventsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val events: List<EventListElement>? = null
)

/*@Serializable
data class ViewFilteredEventsListRequest(
    val title: String,
    val city: String,
    val sportType: String,
    val date: String,
    val maxAmountOfPeople: Int,
    val creatorRating: Double
)*/

@Serializable
data class ViewEventResponse @OptIn(ExperimentalTime::class) constructor(
    val success: Boolean,
    val errorMessage: String? = null,
    val creatorId: Int? = null,
    val participants: List<Person>? = null,
    val title: String? = null,
    val description: String? = null,
    val dateTime: Instant? = null,
    val currentAmountOfPeople: Int? = null,
    val maxAmountOfPeople: Int? = null,
    val sportType: String? = null,
    val photoId: Int? = null
)

//@Serializable
//data class ViewEventRequest(
//    val eventId: Int
//)

@Serializable
data class JoinApplicationResponse(
    val success: Boolean,
    val errorMessage: String? = null
)

@Serializable
data class JoinApplicationRequest(
    val eventId: Int
)

/*@Serializable
data class SomeoneWantsToJoin( // do we need this?
    val eventId: Int,
    val userId: Int,
    val userName: String,
    val userSurname: String,
    val title: String,
    val date: String
)*/

@Serializable
data class Notification( //todo: add more fields
    val eventId: Int? = null,
    val otherUserId: Int? = null,
)

//@Serializable
//data class OpenNotificationsRequest(
//    val userId: Int
//)

@Serializable
data class OpenNotificationsResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val notifications: List<Notification>? = null
)

@Serializable
data class EventApplication @OptIn(ExperimentalTime::class) constructor(
    val eventId: Int,
    val title: String,
    val dateTime: Instant?,
    val maxAmountOfPeople: Int,
    val currentAmountOfPeople: Int
)

//@Serializable
//data class OpenApplicationListRequest( //Applications made by user
//    val userId: Int,
//    val hasEventPassed: Boolean? = null
//)

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
    val surname: String,
    val rating: Double? = null
)

@Serializable
data class GetPersonsListResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val persons: List<Person>? = null
)

//@Serializable
//data class GetPersonsListRequest(
//    val eventId: Int? = null
//)

@Serializable
data class RateRequest(
    val ratedUserId: Int,
    val rating: Double,
    val eventId: Int?=null,
    //todo add boolean for every stat
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