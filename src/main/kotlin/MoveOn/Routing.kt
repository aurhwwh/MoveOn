package MoveOn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import MoveOn.database.Database
import MoveOn.security.Security
import java.sql.Date
import java.sql.Time

fun Application.configureRouting() {
    routing {
        post("/register") {
            val request = try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, RegisterResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                val userId = Database.transaction { conn ->
                    val checkSql = "SELECT id FROM users WHERE email = ?"
                    conn.prepareStatement(checkSql).use { stmt ->
                        stmt.setString(1, request.email)
                        val rs = stmt.executeQuery()
                        if (rs.next()) return@transaction null
                    }

                    val hash = Security.hashPassword(request.password)

                    val insertSql = """
                        INSERT INTO users (user_name, user_surname, date_of_birth, email, password_hash, gender)
                        VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                    """.trimIndent()
                    conn.prepareStatement(insertSql).use { stmt ->
                        stmt.setString(1, request.userName)
                        stmt.setString(2, request.userSurname)
                        stmt.setDate(3, Date.valueOf(request.dateOfBirth))
                        stmt.setString(4, request.email)
                        stmt.setString(5, hash)
                        stmt.setString(6, request.gender)
                        val rs = stmt.executeQuery()
                        rs.next()
                        rs.getInt(1)
                    }
                }

                if (userId != null) {
                    call.respond(HttpStatusCode.OK, RegisterResponse(true))
                } else {
                    call.respond(HttpStatusCode.Conflict, RegisterResponse(false, "User with this email already exists"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, RegisterResponse(false, "Database error: ${e.message}"))
            }
        }

        post("/login") {
            val request = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, LoginResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                val user = Database.useConnection { conn ->
                    val sql = "SELECT id, password_hash FROM users WHERE email = ?"
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setString(1, request.email)
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            Pair(rs.getInt("id"), rs.getString("password_hash"))
                        } else null
                    }
                }

                if (user != null && Security.verifyPassword(request.password, user.second)) {
                    call.respond(HttpStatusCode.OK, LoginResponse(true, userId = user.first))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, LoginResponse(false, "Invalid credentials"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, LoginResponse(false, "Database error: ${e.message}"))
            }
        }

        get("/view_user_profile") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ViewProfileResponse(false, "userId is required"))
                return@get
            }

            try {
                val profile = Database.useConnection { conn ->
                    val sql = """
                        SELECT u.id, u.user_name, u.user_surname, u.date_of_birth, u.description, u.photo_id,
                               COALESCE(AVG(r.rating), 0.0) as avg_rating,
                               (SELECT COUNT(*) FROM friends 
                                WHERE (user_id = u.id OR friend_id = u.id) AND status = 'accepted') as friends_count
                        FROM users u
                        LEFT JOIN ratings r ON u.id = r.to_user_id
                        WHERE u.id = ?
                        GROUP BY u.id
                    """.trimIndent()

                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setInt(1, userId)
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            ViewProfileResponse(
                                success = true,
                                userName = rs.getString("user_name"),
                                userSurname = rs.getString("user_surname"),
                                dateOfBirth = rs.getDate("date_of_birth")?.toString(),
                                description = rs.getString("description"),
                                rating = rs.getDouble("avg_rating"),
                                friendsAmount = rs.getInt("friends_count"),
                                photoId = rs.getInt("photo_id").takeIf { !rs.wasNull() }
                            )
                        } else null
                    }
                }

                if (profile != null) {
                    call.respond(HttpStatusCode.OK, profile)
                } else {
                    call.respond(HttpStatusCode.NotFound, ViewProfileResponse(false, "User not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ViewProfileResponse(false, "Database error: ${e.message}"))
            }
        }

        post("/create_event") {
            val request = try {
                call.receive<CreateEventRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, CreateEventResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                val eventId = Database.transaction { conn ->
                    val sql = """
                        INSERT INTO events (title, description, time, date, city, max_amount_of_people, sport_type, creator_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                    """.trimIndent()
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setString(1, request.title)
                        stmt.setString(2, request.description)
                        stmt.setTime(3, Time.valueOf(request.time))
                        stmt.setDate(4, Date.valueOf(request.date))
                        stmt.setString(5, "Unknown")
                        stmt.setInt(6, request.maxAmountOfPeople)
                        stmt.setString(7, request.sportType)
                        stmt.setInt(8, request.creatorId)
                        val rs = stmt.executeQuery()
                        rs.next()
                        rs.getInt(1)
                    }
                }

                call.respond(HttpStatusCode.OK, CreateEventResponse(true, eventId = eventId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, CreateEventResponse(false, "Database error: ${e.message}"))
            }
        }

        post("/view_filtered_events_list") {
            val request = try {
                call.receive<ViewFilteredEventsListRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ViewFilteredEventsListResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                val events = Database.useConnection { conn ->
                    val sqlBuilder = StringBuilder("""
                        SELECT e.id, e.title, e.city, e.sport_type, e.date, e.max_amount_of_people,
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount,
                               COALESCE((SELECT AVG(rating) FROM ratings WHERE to_user_id = e.creator_id), 0.0) as creator_rating
                        FROM events e
                        WHERE 1=1
                    """.trimIndent())

                    val conditions = mutableListOf<String>()
                    val params = mutableListOf<Any>()

                    if (request.title.isNotBlank()) {
                        conditions.add("e.title ILIKE ?")
                        params.add("%${request.title}%")
                    }
                    if (request.city.isNotBlank()) {
                        conditions.add("e.city ILIKE ?")
                        params.add("%${request.city}%")
                    }
                    if (request.sportType.isNotBlank()) {
                        conditions.add("e.sport_type = ?")
                        params.add(request.sportType)
                    }
                    if (request.date.isNotBlank()) {
                        conditions.add("e.date = ?")
                        params.add(Date.valueOf(request.date))
                    }
                    if (request.maxAmountOfPeople > 0) {
                        conditions.add("e.max_amount_of_people <= ?")
                        params.add(request.maxAmountOfPeople)
                    }

                    if (conditions.isNotEmpty()) {
                        sqlBuilder.append(" AND ").append(conditions.joinToString(" AND "))
                    }

                    sqlBuilder.append(" ORDER BY e.date ASC")

                    val sql = sqlBuilder.toString()
                    conn.prepareStatement(sql).use { stmt ->
                        params.forEachIndexed { index, value ->
                            when (value) {
                                is String -> stmt.setString(index + 1, value)
                                is Date -> stmt.setDate(index + 1, value)
                                is Int -> stmt.setInt(index + 1, value)
                            }
                        }
                        val rs = stmt.executeQuery()
                        val result = mutableListOf<EventListElement>()
                        while (rs.next()) {
                            val creatorRating = rs.getDouble("creator_rating")
                            if (request.creatorRating > 0.0 && creatorRating < request.creatorRating) continue

                            result.add(
                                EventListElement(
                                    eventId = rs.getInt("id"),
                                    title = rs.getString("title"),
                                    city = rs.getString("city"),
                                    sportType = rs.getString("sport_type"),
                                    date = rs.getDate("date").toString(),
                                    maxAmountOfPeople = rs.getInt("max_amount_of_people"),
                                    currentAmountOfPeople = rs.getInt("current_amount"),
                                    creatorRating = creatorRating
                                )
                            )
                        }
                        result
                    }
                }

                call.respond(HttpStatusCode.OK, ViewFilteredEventsListResponse(true, events = events))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ViewFilteredEventsListResponse(false, "Database error: ${e.message}"))
            }
        }

        get("/view_event") {
            val eventId = call.request.queryParameters["eventId"]?.toIntOrNull()
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, ViewEventResponse(false, "eventId is required"))
                return@get
            }

            try {
                val eventData = Database.useConnection { conn ->
                    val sql = """
                        SELECT e.id, e.title, e.description, e.time, e.date, e.max_amount_of_people, e.sport_type, e.creator_id,
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount
                        FROM events e
                        WHERE e.id = ?
                    """.trimIndent()
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setInt(1, eventId)
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            val participantIds = conn.prepareStatement(
                                "SELECT user_id FROM event_participants WHERE event_id = ? AND status = 'accepted'"
                            ).use { pstmt ->
                                pstmt.setInt(1, eventId)
                                val prs = pstmt.executeQuery()
                                val ids = mutableListOf<Int>()
                                while (prs.next()) ids.add(prs.getInt("user_id"))
                                ids
                            }

                            ViewEventResponse(
                                success = true,
                                creatorId = rs.getInt("creator_id"),
                                participantIds = participantIds,
                                title = rs.getString("title"),
                                description = rs.getString("description"),
                                time = rs.getTime("time")?.toString(),
                                date = rs.getDate("date")?.toString(),
                                currentAmountOfPeople = rs.getInt("current_amount"),
                                maxAmountOfPeople = rs.getInt("max_amount_of_people"),
                                sportType = rs.getString("sport_type")
                            )
                        } else null
                    }
                }

                if (eventData != null) {
                    call.respond(HttpStatusCode.OK, eventData)
                } else {
                    call.respond(HttpStatusCode.NotFound, ViewEventResponse(false, "Event not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ViewEventResponse(false, "Database error: ${e.message}"))
            }
        }

        post("/join_application") {
            val request = try {
                call.receive<JoinApplicationRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, JoinApplicationResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                val success = Database.transaction { conn ->
                    val checkSql = "SELECT status FROM event_participants WHERE event_id = ? AND user_id = ?"
                    conn.prepareStatement(checkSql).use { stmt ->
                        stmt.setInt(1, request.eventId)
                        stmt.setInt(2, request.userId)
                        val rs = stmt.executeQuery()
                        if (rs.next()) return@transaction false
                    }

                    val insertSql = "INSERT INTO event_participants (event_id, user_id, status) VALUES (?, ?, 'pending')"
                    conn.prepareStatement(insertSql).use { stmt ->
                        stmt.setInt(1, request.eventId)
                        stmt.setInt(2, request.userId)
                        stmt.executeUpdate()
                    }

                    val creatorSql = "SELECT creator_id FROM events WHERE id = ?"
                    conn.prepareStatement(creatorSql).use { stmt ->
                        stmt.setInt(1, request.eventId)
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            val creatorId = rs.getInt("creator_id")
                            val notifSql = """
                                INSERT INTO notifications (user_id, type, event_id, other_user_id)
                                VALUES (?, 'join_request', ?, ?)
                            """.trimIndent()
                            conn.prepareStatement(notifSql).use { nstmt ->
                                nstmt.setInt(1, creatorId)
                                nstmt.setInt(2, request.eventId)
                                nstmt.setInt(3, request.userId)
                                nstmt.executeUpdate()
                            }
                        }
                    }
                    true
                }

                if (success) {
                    call.respond(HttpStatusCode.OK, JoinApplicationResponse(true))
                } else {
                    call.respond(HttpStatusCode.Conflict, JoinApplicationResponse(false, "Application already exists"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, JoinApplicationResponse(false, "Database error: ${e.message}"))
            }
        }

        // Уведомления
        get("/open_notifications") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, OpenNotificationsResponse(false, "userId is required"))
                return@get
            }

            try {
                val notifications = Database.useConnection { conn ->
                    val sql = """
                        SELECT id, time, type, event_id, other_user_id
                        FROM notifications
                        WHERE user_id = ? AND is_read = false
                        ORDER BY time DESC
                    """.trimIndent()
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setInt(1, userId)
                        val rs = stmt.executeQuery()
                        val list = mutableListOf<Notification>()
                        while (rs.next()) {
                            list.add(
                                Notification(
                                    eventId = rs.getInt("event_id").takeIf { !rs.wasNull() },
                                    otherUserId = rs.getInt("other_user_id").takeIf { !rs.wasNull() }
                                )
                            )
                        }
                        list
                    }
                }

                call.respond(HttpStatusCode.OK, OpenNotificationsResponse(true, notifications = notifications))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, OpenNotificationsResponse(false, "Database error: ${e.message}"))
            }
        }

        get("/open_application_list") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            val hasEventPassed = call.request.queryParameters["hasEventPassed"]?.toBoolean()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, OpenApplicationListResponse(false, "userId is required"))
                return@get
            }

            try {
                val applications = Database.useConnection { conn ->
                    val sqlBuilder = StringBuilder("""
                        SELECT e.id, e.title, e.date, e.max_amount_of_people,
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount
                        FROM events e
                        JOIN event_participants ep ON e.id = ep.event_id
                        WHERE ep.user_id = ? AND ep.status = 'pending'
                    """.trimIndent())

                    val params = mutableListOf<Any>(userId)

                    if (hasEventPassed != null) {
                        if (hasEventPassed) {
                            sqlBuilder.append(" AND e.date < CURRENT_DATE")
                        } else {
                            sqlBuilder.append(" AND e.date >= CURRENT_DATE")
                        }
                    }

                    sqlBuilder.append(" ORDER BY e.date ASC")

                    conn.prepareStatement(sqlBuilder.toString()).use { stmt ->
                        params.forEachIndexed { index, value ->
                            stmt.setInt(index + 1, value as Int)
                        }
                        val rs = stmt.executeQuery()
                        val list = mutableListOf<EventApplication>()
                        while (rs.next()) {
                            list.add(
                                EventApplication(
                                    eventId = rs.getInt("id"),
                                    title = rs.getString("title"),
                                    date = rs.getDate("date").toString(),
                                    maxAmountOfPeople = rs.getInt("max_amount_of_people"),
                                    currentAmountOfPeople = rs.getInt("current_amount")
                                )
                            )
                        }
                        list
                    }
                }

                call.respond(HttpStatusCode.OK, OpenApplicationListResponse(true, eventApplications = applications))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, OpenApplicationListResponse(false, "Database error: ${e.message}"))
            }
        }

        get("/get_persons_list") {
            val eventId = call.request.queryParameters["eventId"]?.toIntOrNull()
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, GetPersonsListResponse(false, "eventId is required"))
                return@get
            }

            try {
                val persons = Database.useConnection { conn ->
                    val sql = """
                        SELECT u.id, u.user_name, u.user_surname
                        FROM users u
                        JOIN event_participants ep ON u.id = ep.user_id
                        WHERE ep.event_id = ? AND ep.status = 'accepted'
                    """.trimIndent()
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setInt(1, eventId)
                        val rs = stmt.executeQuery()
                        val list = mutableListOf<Person>()
                        while (rs.next()) {
                            list.add(
                                Person(
                                    id = rs.getInt("id"),
                                    name = rs.getString("user_name"),
                                    surname = rs.getString("user_surname")
                                )
                            )
                        }
                        list
                    }
                }

                call.respond(HttpStatusCode.OK, GetPersonsListResponse(true, persons = persons))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, GetPersonsListResponse(false, "Database error: ${e.message}"))
            }
        }

        post("/rate") {
            val request = try {
                call.receive<RateRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, RateResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                Database.transaction { conn ->
                    val sql = "INSERT INTO ratings (from_user_id, to_user_id, event_id, rating) VALUES (?, ?, ?, ?)"
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setInt(1, request.userWhoRatesId)
                        stmt.setInt(2, request.ratedUserId)
                        if (request.eventId != null) {
                            stmt.setInt(3, request.eventId)
                        } else {
                            stmt.setNull(3, java.sql.Types.INTEGER)
                        }
                        stmt.setDouble(4, request.rating)
                        stmt.executeUpdate()
                    }
                }

                call.respond(HttpStatusCode.OK, RateResponse(true))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, RateResponse(false, "Database error: ${e.message}"))
            }
        }

        post("/accept_or_decline_event_application") {
            val request = try {
                call.receive<AcceptOrDeclineEventApplicationRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, AcceptOrDeclineEventApplicationResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }

            try {
                val success = Database.transaction { conn ->
                    val newStatus = if (request.isAccepted) "accepted" else "declined"
                    val updateSql = "UPDATE event_participants SET status = ? WHERE event_id = ? AND user_id = ?"
                    val updatedRows = conn.prepareStatement(updateSql).use { stmt ->
                        stmt.setString(1, newStatus)
                        stmt.setInt(2, request.eventId)
                        stmt.setInt(3, request.userId)
                        stmt.executeUpdate()
                    }
                    if (updatedRows == 0) return@transaction false

                    val notifType = if (request.isAccepted) "application_accepted" else "application_declined"
                    val notifSql = """
                        INSERT INTO notifications (user_id, type, event_id, other_user_id)
                        VALUES (?, ?, ?, ?)
                    """.trimIndent()
                    conn.prepareStatement(notifSql).use { stmt ->
                        stmt.setInt(1, request.userId)
                        stmt.setString(2, notifType)
                        stmt.setInt(3, request.eventId)
                        stmt.setNull(4, java.sql.Types.INTEGER)
                        stmt.executeUpdate()
                    }
                    true
                }

                if (success) {
                    call.respond(HttpStatusCode.OK, AcceptOrDeclineEventApplicationResponse(true))
                } else {
                    call.respond(HttpStatusCode.NotFound, AcceptOrDeclineEventApplicationResponse(false, "Application not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, AcceptOrDeclineEventApplicationResponse(false, "Database error: ${e.message}"))
            }
        }
    }
}