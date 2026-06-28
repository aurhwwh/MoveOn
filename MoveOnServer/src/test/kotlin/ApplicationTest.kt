package MoveOn

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class ApplicationTest {

    private fun ApplicationTestBuilder.jsonClient() =
        createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun routeOptions_invalidParams_returnsError() = testApplication {

        application { module() }

        val client = jsonClient()

        val response = client.get("/route_options")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<RouteOptionsResponse>()
        assertEquals(false, body.success)
    }

    @Test
    fun routeOptions_validRequest_returnsRoutes() = testApplication {

        application { module() }

        val client = jsonClient()

        val response = client.get {
            url(
                "/route_options?lat=59.93&lon=30.31&radius=500"
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<RouteOptionsResponse>()

        assertTrue(body.success)
        assertNotNull(body.centralPoint)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun register_newUser_success() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "test_${System.currentTimeMillis()}@mail.com"

        val response = client.post("/register") {
            contentType(ContentType.Application.Json)

            setBody(
                RegisterRequest(
                    userName = "Test",
                    userSurname = "User",
                    dateOfBirth = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    email = email,
                    password = "123456",
                    gender = "male"
                )
            )
        }

        assertEquals(
            HttpStatusCode.OK,
            response.status
        )

        val body = response.body<RegisterResponse>()

        assertTrue(body.success)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun register_existingEmail_returnsConflict() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "duplicate@mail.com"

        val request = RegisterRequest(
            userName = "A",
            userSurname = "B",
            dateOfBirth = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            email = email,
            password = "123456",
            gender = "male"
        )

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val response = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(
            HttpStatusCode.Conflict,
            response.status
        )
    }

    @Test
    fun register_invalidJson_returnsBadRequest() = testApplication {

        application { module() }

        val client = jsonClient()

        val response = client.post("/register") {
            contentType(ContentType.Application.Json)

            setBody(
                """
            {
              "email": "abc"
            }
            """
            )
        }

        assertEquals(
            HttpStatusCode.BadRequest,
            response.status
        )
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun login_returnsTokens() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "login_${System.currentTimeMillis()}@mail.com"

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    userName = "Test",
                    userSurname = "User",
                    dateOfBirth = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    email = email,
                    password = "123456",
                    gender = "male"
                )
            )
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = email,
                    password = "123456"
                )
            )
        }

        assertEquals(
            HttpStatusCode.OK,
            response.status
        )

        val body = response.body<LoginResponse>()

        assertTrue(body.success)
        assertNotNull(body.accessToken)
        assertNotNull(body.refreshToken)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun login_wrongPassword_returnsUnauthorized() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "wrong_${System.currentTimeMillis()}@mail.com"

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    userName = "Test",
                    userSurname = "User",
                    dateOfBirth = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    email = email,
                    password = "123456",
                    gender = "male"
                )
            )
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = email,
                    password = "wrong"
                )
            )
        }

        assertEquals(
            HttpStatusCode.Unauthorized,
            response.status
        )
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun refresh_validToken_returnsNewTokens() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "refresh_${System.currentTimeMillis()}@mail.com"

        client.post("/register") {
            contentType(ContentType.Application.Json)

            setBody(
                RegisterRequest(
                    userName = "Test",
                    userSurname = "User",
                    dateOfBirth = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    email = email,
                    password = "123456",
                    gender = "male"
                )
            )
        }

        val login =
            client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        email=email,
                        password="123456"
                    )
                )
            }
                .body<LoginResponse>()

        val refresh =
            client.post("/refresh") {
                contentType(ContentType.Application.Json)
                setBody(
                    RefreshRequest(
                        oldRefreshToken =
                            login.refreshToken!!
                    )
                )
            }

        assertEquals(
            HttpStatusCode.OK,
            refresh.status
        )

        val body = refresh.body<RefreshResponse>()

        assertTrue(body.success)
        assertNotNull(body.newAccessToken)
        assertNotNull(body.newRefreshToken)
    }

    @Test
    fun viewFilteredEvents_returnsList() = testApplication {

        application { module() }

        val client = jsonClient()

        val response = client.get("/view_filtered_events_list?page=0&limit=10")

        assertEquals(
            HttpStatusCode.OK,
            response.status
        )

        val body = response.body<ViewFilteredEventsListResponse>()

        assertTrue(body.success)
    }

    @Test
    fun getPersonsList_withoutEventId_returnsBadRequest() = testApplication {
        application { module() }

        client.get("/get_persons_list").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun viewMyProfile_withoutToken_returnsUnauthorized() = testApplication {
        application { module() }

        client.get("/view_my_profile").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

    @Test
    fun viewMyProfile_withToken_returnsProfile() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "test_${System.currentTimeMillis()}@mail.com"

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    userName = "Test",
                    userSurname = "User",
                    email = email,
                    password = "123456",
                    dateOfBirth = LocalDate(2000,1,1),
                    gender = "male"
                )
            )
        }

        val loginResponse = client.post("/login") {
            contentType(ContentType.Application.Json)

            setBody(
                LoginRequest(
                    email = email,
                    password = "123456"
                )
            )
        }.body<LoginResponse>()

        val token = loginResponse.accessToken

        client.get("/view_my_profile") {
            header(
                HttpHeaders.Authorization,
                "Bearer $token"
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val body = body<ViewProfileResponse>()
            assertTrue(body.success)
            assertEquals("Test", body.userName)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun createEvent_withoutToken_returnsUnauthorized() = testApplication {
        application { module() }

        val client = jsonClient()

        client.post("/create_event") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateEventRequest(
                    title = "Test",
                    description = "desc",
                    dateTime = Clock.System.now(),
                    city = "Lelystad",
                    place = "Park",
                    lat = 52.5,
                    lon = 5.5,
                    maxAmountOfPeople = 10,
                    sportType = "Running"
                )
            )
        }.apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun createEvent_createsEvent() = testApplication {

        application { module() }

        val client = jsonClient()

        val email = "event_${System.currentTimeMillis()}@test.com"

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    userName = "Event",
                    userSurname = "Creator",
                    email = email,
                    password = "123456",
                    dateOfBirth = LocalDate(2000,1,1),
                    gender = "M"
                )
            )
        }

        val login = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = email,
                    password = "123456"
                )
            )
        }.body<LoginResponse>()

        val token = login.accessToken

        val response = client.post("/create_event") {

            header(
                HttpHeaders.Authorization,
                "Bearer $token"
            )
            contentType(ContentType.Application.Json)

            setBody(
                CreateEventRequest(
                    title = "Test event",
                    description = "Test description",
                    dateTime = Clock.System.now(),
                    city = "Lelystad",
                    place = "Park",
                    lat = 52.518,
                    lon = 5.471,
                    maxAmountOfPeople = 10,
                    sportType = "Running"
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<CreateEventResponse>()

        assertTrue(body.success)
        assertNotNull(body.eventId)
    }
}
