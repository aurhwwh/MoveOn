package MoveOn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Date


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
        }
        post("/login") {
            val request = runCatching { call.receive<LoginRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        LoginResponse(false, "Invalid JSON format for login")
                    )
                    return@post
                }
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(true, userId = 1)
            )
        }
        post("/view_user_profile"){
            val request = runCatching { call.receive<ViewProfileRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ViewProfileResponse(false, "Invalid JSON format for viewing profile")
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
        }
        post("/create_event"){
            val request = runCatching { call.receive<CreateEventRequest>() }
                .getOrElse {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        CreateEventResponse(false, "Invalid JSON format for viewing profile")
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
        }
    }
}
