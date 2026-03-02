package MoveOn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Date
import java.sql.Time


fun Application.configureRouting() {
    routing {
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
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(true, userId = 1)
            )
            return@post
        }
        post("/view_user_profile"){
            val request = runCatching { call.receive<ViewProfileRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ViewProfileResponse(
                            false,
                            "Invalid JSON format for viewing profile"
                        )
                    )
                    return@post
                }
            call.respond(
                HttpStatusCode.OK,
                ViewProfileResponse(true,
                    null,
                    1,
                    "Ivan",
                    "Ivanov",
                    Date(0),
                    "no description",
                    5.0,
                    0
                )
            )
            return@post
        }
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
        post("/view_filtered_events_list"){
            val request = runCatching { call.receive<ViewFilteredEventsListRequest>() }
            .getOrElse {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ViewFilteredEventsListResponse(
                        false,
                        "Invalid JSON format for viewing filtered events"
                    )
                )
                return@post
            }
            call.respond(
                HttpStatusCode.OK,
                ViewFilteredEventsListResponse(true,
                    null,
                    listOf(EventListElement(
                        1,
                        "Lets play",
                        "Moscow",
                        "Volleyball",
                        Date(0),
                        8,
                        6,
                        5.0
                        )
                    )
                )
            )
            return@post
        }
        post("/view_event"){
            val request = runCatching { call.receive<ViewEventRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ViewEventResponse(
                            false,
                            "Invalid JSON format for viewing event"
                        )
                    )
                return@post
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
                    Time(0),
                    Date(0),
                    1,
                    8,
                    "Volleyball"
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
        post("/open_notifications"){
            val request = runCatching { call.receive<OpenNotificationsRequest>() }
            .getOrElse {
                call.respond(
                    HttpStatusCode.BadRequest,
                    OpenNotificationsResponse(
                        false,
                        "Invalid JSON format for open notifications"
                    )
                )
                return@post
            }
            call.respond(
                HttpStatusCode.OK,
                OpenNotificationsResponse(
                    true,
                    null,
                    listOf(Notification(
                        1,
                        1
                    ))
                )
            )
            return@post
        }
        post("/open_application_list"){
            val request = runCatching { call.receive<OpenApplicationListRequest>() }
                .getOrElse {
                    call.respond(HttpStatusCode.BadRequest,
                        OpenApplicationListResponse(
                            false,
                            "Invalid JSON format for open application list"
                        )
                    )
                    return@post
                }
            call.respond(
                HttpStatusCode.OK,
                OpenApplicationListResponse(
                    true,
                    null,
                    listOf(EventApplication(
                        1,
                        "Lets play",
                        Date(0),
                        8,
                        1
                    ))
                )
            )
            return@post
        }
        post("/get_persons_list") {
            val request = runCatching{ call.receive<GetPersonsListRequest>()}
            .getOrElse {
                call.respond(
                    HttpStatusCode.BadRequest,
                    GetPersonsListResponse(
                        false,
                        "Invalid JSON format for getting persons"
                    )
                )
                return@post
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
            return@post
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
