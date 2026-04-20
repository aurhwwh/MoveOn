package MoveOn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import MoveOn.database.Database
import MoveOn.security.Security
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.sql.Date
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@OptIn(ExperimentalTime::class)
fun Application.configureRouting() {
    routing {
        get("/"){
            call.respondText("Server running")
        }
        post("/refresh") {
            val request = try {
                call.receive<RefreshRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, RefreshResponse(false, "Invalid JSON: ${e.message}"))
                return@post
            }
            val userId = getUserIdFromJWT(request.oldRefreshToken)
            if(userId == null || !isRefreshToken(request.oldRefreshToken)) {
                call.respond(HttpStatusCode.Unauthorized, RefreshResponse(false,"Invalid Refresh Token") )
                return@post
            }
            try {
                val databaseHash = Database.transaction { conn ->
                    val sql = "SELECT refresh_token_hash FROM users WHERE id = ?"
                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setInt(1, userId)
                        val rs = stmt.executeQuery()
                        if (rs.next()) {
                            rs.getString("refresh_token_hash")
                        } else null
                    }
                }
                if (databaseHash == null || !verifyRefreshToken(request.oldRefreshToken, databaseHash)) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        RefreshResponse(false, "Refresh token not valid")
                    )
                    return@post
                }
                val newAccessToken = generateAccessToken(userId)
                val newRefreshToken = generateRefreshToken(userId)
                val newHash = hashRefreshToken(newRefreshToken)
                Database.transaction { conn ->
                    val sql = "UPDATE users SET refresh_token_hash = ? WHERE id = ?"

                    conn.prepareStatement(sql).use { stmt ->
                        stmt.setString(1, newHash)
                        stmt.setInt(2, userId)
                        stmt.executeUpdate()
                    }
                }
                call.respond(
                    HttpStatusCode.OK,
                    RefreshResponse(true, null, newRefreshToken, newAccessToken)
                )


            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, RefreshResponse(false, "Database error: ${e.message}"))
            }

        }
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
                        stmt.setObject(3, request.dateOfBirth.toJavaLocalDate())
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
                    val refreshToken = generateRefreshToken(user.first)
                    val refreshHash = hashRefreshToken(refreshToken)
                    Database.transaction { conn ->
                        val sql = "UPDATE users SET refresh_token_hash = ? WHERE id = ?"

                        conn.prepareStatement(sql).use { stmt ->
                            stmt.setString(1, refreshHash)
                            stmt.setInt(2, user.first)
                            stmt.executeUpdate()
                        }
                    }
                    call.respond(HttpStatusCode.OK, LoginResponse(true, accessToken = generateAccessToken(user.first), refreshToken = refreshToken))
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
                                dateOfBirth = rs.getDate("date_of_birth")?.toLocalDate()?.toKotlinLocalDate(),
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



        get("/view_filtered_events_list") {
            val title = call.request.queryParameters["title"]
            val city = call.request.queryParameters["city"]
            val sportType = call.request.queryParameters["sportType"]
            val datetime = call.request.queryParameters["datetime"]?.toLong()
            val maxAmountOfPeople = call.request.queryParameters["maxAmountOfPeople"]?.toInt()
            val creatorRating = call.request.queryParameters["creatorRating"]?.toDouble()
            val page = call.request.queryParameters["page"]?.toInt()
            val limit = call.request.queryParameters["limit"]?.toInt()
            if (page==null || limit == null){
                call.respond(
                    HttpStatusCode.BadRequest,
                    ViewProfileResponse(false,
                        "Limit and page are required")
                )
                return@get
            }

            try {
                val events = Database.useConnection { conn ->
                    val sqlBuilder = StringBuilder("""
                        SELECT e.id, e.title, e.description, e.city, e.sport_type, e.time, e.max_amount_of_people,  
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount,
                               COALESCE((SELECT AVG(rating) FROM ratings WHERE to_user_id = e.creator_id), 0.0) as creator_rating
                        FROM events e
                        WHERE 1=1
                    """.trimIndent())

                    val conditions = mutableListOf<String>()
                    val params = mutableListOf<Any>()

                    if (title != null) {
                        conditions.add("e.title ILIKE ?")
                        params.add("%${title}%")
                    }
                    if (city!= null) {
                        conditions.add("e.city ILIKE ?")
                        params.add("%${city}%")
                    }
                    if (sportType!= null) {
                        conditions.add("e.sport_type = ?")
                        params.add(sportType)
                    }
                    /*if (datetime != null) {
                        conditions.add("e.date = ?")
                        params.add(Date.valueOf(.date))
                    }*/
                    if (maxAmountOfPeople != null) {
                        conditions.add("e.max_amount_of_people <= ?")
                        params.add(maxAmountOfPeople)
                    }

                    if (conditions.isNotEmpty()) {
                        sqlBuilder.append(" AND ").append(conditions.joinToString(" AND "))
                    }

                    sqlBuilder.append(" ORDER BY e.time ASC")

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
                            val creatorRating1 = rs.getDouble("creator_rating")
                            if (creatorRating != null && creatorRating > 0.0 && creatorRating1 < creatorRating) continue

                            result.add(
                                EventListElement(
                                    eventId = rs.getInt("id"),
                                    title = rs.getString("title"),
                                    city = rs.getString("city"),
                                    sportType = rs.getString("sport_type"),
                                    //date = rs.getDate("date").toString(),
                                    dateTime = rs.getTimestamp("time")?.toInstant()?.toKotlinInstant(),
                                    maxAmountOfPeople = rs.getInt("max_amount_of_people"),
                                    currentAmountOfPeople = rs.getInt("current_amount"),
                                    creatorRating = creatorRating1,
                                    photoId = 1, //todo: add params to db
                                    description = rs.getString("description") ?: ""
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

        authenticate("auth-jwt") {
            get("/view_event") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val eventId = call.request.queryParameters["eventId"]?.toIntOrNull()
                if (eventId == null) {
                    call.respond(HttpStatusCode.BadRequest, ViewEventResponse(false, "eventId is required"))
                    return@get
                }

                try {
                    val eventData = Database.useConnection { conn ->
                        val sql = """
                        SELECT e.id, e.title, e.description, e.time, e.max_amount_of_people, e.sport_type, e.creator_id,
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount
                        FROM events e
                        WHERE e.id = ?
                    """.trimIndent()
                        conn.prepareStatement(sql).use { stmt ->
                            stmt.setInt(1, eventId)
                            val rs = stmt.executeQuery()
                            if (rs.next()) {
                                val participants = conn.prepareStatement(
                                    """SELECT u.id, u.user_name AS name, u.user_surname AS surname,
                                    AVG(r.rating) AS rating,
                                    CASE WHEN u.id = ? THEN 1 ELSE 0 END AS is_creator
                                    FROM users u
                                    JOIN event_participants ep ON u.id = ep.user_id
                                    LEFT JOIN ratings r ON r.to_user_id = u.id
                                    WHERE ep.event_id = ? AND ep.status = 'accepted'
                                    GROUP BY u.id, u.user_name, u.user_surname
                                    ORDER BY is_creator DESC, u.id
                                    """
                                ).use { pstmt ->
                                    pstmt.setInt(1, rs.getInt("creator_id"))
                                    pstmt.setInt(2, eventId)
                                    val rs = pstmt.executeQuery()
                                    val list = mutableListOf<Person>()
                                    var isUserParticipant = false

                                    while (rs.next()) {
                                        val participantId = rs.getInt("id")
                                        if (participantId == userId) {
                                            isUserParticipant = true
                                        }
                                        list.add(
                                            Person(
                                                id = rs.getInt("id"),
                                                name = rs.getString("name"),
                                                surname = rs.getString("surname"),
                                                rating = rs.getDouble("rating")
                                            )
                                        )
                                    }
                                    Pair(list, isUserParticipant)
                                }

                                ViewEventResponse(
                                    success = true,
                                    creatorId = rs.getInt("creator_id"),
                                    participants = participants.first,
                                    title = rs.getString("title"),
                                    description = rs.getString("description"),
                                    dateTime = rs.getTimestamp("time")?.toInstant()?.toKotlinInstant(),
                                    currentAmountOfPeople = rs.getInt("current_amount"),
                                    maxAmountOfPeople = rs.getInt("max_amount_of_people"),
                                    sportType = rs.getString("sport_type"),
                                    isUserCreator = (userId==rs.getInt("creator_id")),
                                    isUserParticipant = participants.second
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

            get("/view_my_profile") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
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
                                    dateOfBirth = rs.getDate("date_of_birth")?.toLocalDate()?.toKotlinLocalDate(),
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
                val principal = call.principal<JWTPrincipal>()
                val creatorId = principal!!.payload.getClaim("userId").asInt()
                try {
                    val eventId = Database.transaction { conn ->

                        val sqlEvent = """
        INSERT INTO events (title, description, time, city, max_amount_of_people, sport_type, creator_id)
        VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id
    """.trimIndent()

                        val id = conn.prepareStatement(sqlEvent).use { stmt ->
                            stmt.setString(1, request.title)
                            stmt.setString(2, request.description)
                            stmt.setTimestamp(3, java.sql.Timestamp.from(request.dateTime.toJavaInstant()))
                            stmt.setString(4, request.city)
                            stmt.setInt(5, request.maxAmountOfPeople)
                            stmt.setString(6, request.sportType)
                            stmt.setInt(7, creatorId)

                            val rs = stmt.executeQuery()
                            rs.next()
                            rs.getInt(1)
                        }

                        val sqlParticipant = """
                            INSERT INTO event_participants (event_id, user_id, status)
                            VALUES (?, ?, 'accepted')
                        """.trimIndent()

                        conn.prepareStatement(sqlParticipant).use { stmt ->
                            stmt.setInt(1, id)
                            stmt.setInt(2, creatorId)
                            stmt.executeUpdate()
                        }

                        id
                    }

                    call.respond(HttpStatusCode.OK, CreateEventResponse(true, eventId = eventId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, CreateEventResponse(false, "Database error: ${e.message}"))
                }
            }
            post("/join_application") {
                val request = try {
                    call.receive<JoinApplicationRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, JoinApplicationResponse(false, "Invalid JSON: ${e.message}"))
                    return@post
                }
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                try {
                    val success = Database.transaction { conn ->
                        val checkSql = "SELECT status FROM event_participants WHERE event_id = ? AND user_id = ?"
                        conn.prepareStatement(checkSql).use { stmt ->
                            stmt.setInt(1, request.eventId)
                            stmt.setInt(2, userId)
                            val rs = stmt.executeQuery()
                            if (rs.next()) return@transaction false
                        }
                        //temporary for mvp
                        //val insertSql = "INSERT INTO event_participants (event_id, user_id, status) VALUES (?, ?, 'pending')"
                        val insertSql = "INSERT INTO event_participants (event_id, user_id, status) VALUES (?, ?, 'accepted')"
                        conn.prepareStatement(insertSql).use { stmt ->
                            stmt.setInt(1, request.eventId)
                            stmt.setInt(2, userId)
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
                                    nstmt.setInt(3, userId)
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
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val hasEventPassed = call.request.queryParameters["hasEventPassed"]?.toBoolean()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, OpenApplicationListResponse(false, "userId is required"))
                    return@get
                }

                try {
                    val applications = Database.useConnection { conn ->
                        val sqlBuilder = StringBuilder("""
                        SELECT e.id, e.title, e.time, e.max_amount_of_people,
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount
                        FROM events e
                        JOIN event_participants ep ON e.id = ep.event_id
                        WHERE ep.user_id = ? AND ep.status = 'pending'
                    """.trimIndent())

                        val params = mutableListOf<Any>(userId)

                        if (hasEventPassed != null) {
                            if (hasEventPassed) {
                                sqlBuilder.append(" AND e.time < CURRENT_TIMESTAMP")
                            } else {
                                sqlBuilder.append(" AND e.time >= CURRENT_TIMESTAMP")
                            }
                        }

                        sqlBuilder.append(" ORDER BY e.time ASC")

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
                                        dateTime = rs.getTimestamp("time")?.toInstant()?.toKotlinInstant(),
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
            post("/rate") {
                val request = try {
                    call.receive<RateRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, RateResponse(false, "Invalid JSON: ${e.message}"))
                    return@post
                }
                val principal = call.principal<JWTPrincipal>()
                val userWhoRatesId = principal!!.payload.getClaim("userId").asInt()

                try {
                    Database.transaction { conn ->
                        val sql = "INSERT INTO ratings (from_user_id, to_user_id, event_id, rating) VALUES (?, ?, ?, ?)"
                        conn.prepareStatement(sql).use { stmt ->
                            stmt.setInt(1, userWhoRatesId)
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
                val principal = call.principal<JWTPrincipal>()
                val creatorId = principal!!.payload.getClaim("userId").asInt()//todo add check for creating this event

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


            get("/view_my_events_list") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, ViewMyEventsListResponse(false, "userId is required"))
                    return@get
                }

                try {
                    val events = Database.useConnection { conn ->
                        val sqlBuilder = StringBuilder("""
                        SELECT e.id, e.title, e.description, e.city, e.sport_type, e.time, e.max_amount_of_people,  
                               (SELECT COUNT(*) FROM event_participants WHERE event_id = e.id AND status = 'accepted') as current_amount,
                               COALESCE((SELECT AVG(rating) FROM ratings WHERE to_user_id = e.creator_id), 0.0) as creator_rating,
                               (e.creator_id = ?) as is_creator
                        FROM events e
                        WHERE 1=1
                        AND EXISTS (
                            SELECT 1 FROM event_participants ep WHERE ep.event_id = e.id AND ep.user_id = ?
                        )
                         AND e.time > CURRENT_TIMESTAMP
                    """.trimIndent())

                        sqlBuilder.append(" ORDER BY e.time ASC")

                        val sql = sqlBuilder.toString()
                        conn.prepareStatement(sql).use { stmt ->
                            stmt.setInt(1, userId)
                            stmt.setInt(2, userId)

                            val rs = stmt.executeQuery()
                            val result = mutableListOf<EventListElement>()
                            while (rs.next()) {
                                val creatorRating1 = rs.getDouble("creator_rating")

                                result.add(
                                    EventListElement(
                                        eventId = rs.getInt("id"),
                                        title = rs.getString("title"),
                                        city = rs.getString("city"),
                                        sportType = rs.getString("sport_type"),
                                        dateTime = rs.getTimestamp("time")?.toInstant()?.toKotlinInstant(),
                                        maxAmountOfPeople = rs.getInt("max_amount_of_people"),
                                        currentAmountOfPeople = rs.getInt("current_amount"),
                                        creatorRating = creatorRating1,
                                        photoId = 1, //todo: add params to db
                                        description = rs.getString("description") ?: "",
                                        isCreator = rs.getBoolean("is_creator")
                                    )
                                )
                            }
                            result
                        }
                    }

                    call.respond(HttpStatusCode.OK, ViewMyEventsListResponse(true, events = events))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ViewMyEventsListResponse(false, "Database error: ${e.message}"))
                }
            }
        }
    }
}