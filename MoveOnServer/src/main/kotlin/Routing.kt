package MoveOn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        post("/refresh") {
            val request = runCatching {call.receive<RefreshRequest>()}
                .getOrElse { call.respond(
                    HttpStatusCode.BadRequest,
                    RefreshResponse(false, "Invalid format for refresh")
                    )
                    return@post
                }
            //тут достаем из бд userId и генерим на его основе access токен
            //если его нет то выдаем success = false
            val newAccessToken = generateAccessToken(1)
            val newRefreshToken = generateRefreshToken()
            call.respond(HttpStatusCode.OK, RefreshResponse(true, null,newRefreshToken, newAccessToken))
        }
        post("/register") {
            val request = runCatching { call.receive<RegisterRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        RegisterResponse(false, "Invalid JSON format for register")
                    )
                    return@post
                }
            call.respond(
                HttpStatusCode.OK,
                RegisterResponse(true)
            )
            return@post
        }
        post("/login") {
            val request = runCatching { call.receive<LoginRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        LoginResponse(
                            false,
                            "Invalid JSON format for login"
                        )
                    )
                    return@post
                }
            val refreshToken = generateRefreshToken()
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(true, null, generateAccessToken(1), refreshToken)
            )
            return@post
        }
        get("/view_user_profile"){
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ViewProfileResponse(false, "userId is required")
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                ViewProfileResponse(true,
                    null,
                    1,
                    "Ivan",
                    "Ivanov",
                    "01.01.2001",
                    "no description",
                    5.0,
                    0
                )
            )
            return@get
        }

        get("/view_filtered_events_list"){
            val title = call.request.queryParameters["title"]
            val city = call.request.queryParameters["city"]
            val sportType = call.request.queryParameters["sportType"]
            val datetime = call.request.queryParameters["datetime"]?.toLong()
            val maxAmountOfPeople = call.request.queryParameters["maxAmountOfPeople"]?.toInt()
            val creatorRating = call.request.queryParameters["creatorRating"]?.toDouble()
            call.respond(
                HttpStatusCode.OK,
                ViewFilteredEventsListResponse(true,
                    null,
                    listOf(EventListElement(
                        1,
                        "Lets play",
                        "Moscow",
                        "Volleyball",
                        "01.01.2001",
                        8,
                        6,
                        5.0,
                        1,
                        ""
                    )
                    )
                )
            )
            return@get
        }
        get("/view_event"){
            val eventId = call.request.queryParameters["eventId"]?.toIntOrNull()
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest,
                    ViewEventResponse(false, "eventId is required")
                    )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                ViewEventResponse(
                    true,
                    null,
                    1,
                    listOf(1),
                    "Lets play",
                    "",
                    "23:59:59:59",
                    "01.01.2001",
                    1,
                    8,
                    "Volleyball"
                )
            )
            return@get
        }
        get("/get_persons_list") {
            val eventId = call.request.queryParameters["eventId"]?.toIntOrNull()
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest,
                    GetPersonsListResponse(false, "eventId is required")
                )
            }
            call.respond(
                HttpStatusCode.OK,
                GetPersonsListResponse(
                    true,
                    null,
                    listOf(
                        Person(
                            1,
                            "Ivan",
                            "Ivanov"
                        )
                    )
                )
            )
            return@get
        }
        authenticate("auth-jwt") {
            post("/create_event"){
                val request = runCatching { call.receive<CreateEventRequest>() }
                    .getOrElse {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            CreateEventResponse(
                                false,
                                "Invalid JSON format for creating event"
                            )
                        )
                        return@post
                    }
                call.respond(
                    HttpStatusCode.OK,
                    CreateEventResponse(true,
                        null,
                        1
                    )
                )
                return@post
            }
            post("/join_application"){
                val request = runCatching { call.receive<JoinApplicationRequest>() }
                    .getOrElse {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            JoinApplicationResponse(
                                false,
                                "Invalid JSON format for join application"
                            )
                        )
                        return@post
                    }
                call.respond(
                    HttpStatusCode.OK,
                    JoinApplicationResponse(
                        true
                    )
                )
            }
            get("/open_notifications"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                call.respond(
                    HttpStatusCode.OK,
                    OpenNotificationsResponse(
                        true,
                        userId.toString(),
                        listOf(Notification(
                            1,
                            1
                        ))
                    )
                )
                return@get
            }
            get("/open_application_list"){
                val hasEventPassed = call.request.queryParameters["hasEventPassed"]?.toBoolean()
                call.respond(
                    HttpStatusCode.OK,
                    OpenApplicationListResponse(
                        true,
                        null,
                        listOf(EventApplication(
                            1,
                            "Lets play",
                            "01.01.2001",
                            8,
                            1
                        ))
                    )
                )
                return@get
            }
            post("/rate"){
                val request = runCatching { call.receive<RateRequest>() }
                    .getOrElse {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            RateResponse(
                                false,
                                "Invalid JSON format for rate"
                            )
                        )
                        return@post
                    }
                call.respond(
                    HttpStatusCode.OK,
                    RateResponse(
                        true
                    )
                )
            }
            post("/accept_or_decline_event_application"){
                val request = runCatching{call.receive<AcceptOrDeclineEventApplicationRequest>()}
                    .getOrElse {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            AcceptOrDeclineEventApplicationResponse(
                                false,
                                "Invalid JSON format for accepting or declining application"
                            )
                        )
                    }
                call.respond(
                    HttpStatusCode.OK,
                    AcceptOrDeclineEventApplicationResponse(
                        true
                    )
                )
                return@post
            }
        }
    }
}