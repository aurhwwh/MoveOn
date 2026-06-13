package MoveOn.database

import MoveOn.EventMessage
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant

// Функции расширения для ResultSet (теперь видны везде в пакете)
inline fun <reified T : Any> ResultSet.toObject(transform: (ResultSet) -> T): T? =
    if (next()) transform(this) else null

inline fun <reified T : Any> ResultSet.toList(transform: (ResultSet) -> T): List<T> {
    val list = mutableListOf<T>()
    while (next()) {
        list.add(transform(this))
    }
    return list
}

object Database {
    private val dataSource: HikariDataSource by lazy {
        val config = HikariConfig().apply {
            val host = System.getenv("DB_HOST") ?: "localhost"
            val port = System.getenv("DB_PORT") ?: "5432"
            val db = System.getenv("DB_NAME") ?: "movedb"
            val user = System.getenv("DB_USER") ?: "postgres"
            val password = System.getenv("DB_PASSWORD") ?: "seva1234"

            jdbcUrl = "jdbc:postgresql://$host:$port/$db"
            username = user
            this.password = password

            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        HikariDataSource(config)
    }

    fun <T> useConnection(block: (Connection) -> T): T {
        dataSource.connection.use { connection ->
            return block(connection)
        }
    }

    fun <T> transaction(block: (Connection) -> T): T {
        return useConnection { connection ->
            connection.autoCommit = false
            try {
                val result = block(connection)
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }
}

// DAO для чата события
class ChatDao {
    fun insertMessage(eventId: Int, userId: Int, message: String): Int? = Database.transaction { connection ->
        val sql = """
            INSERT INTO event_messages (event_id, user_id, message, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """.trimIndent()
        val now = Timestamp.from(Instant.now())
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, eventId)
            stmt.setInt(2, userId)
            stmt.setString(3, message)
            stmt.setTimestamp(4, now)
            stmt.setTimestamp(5, now)
            val rs = stmt.executeQuery()
            if (rs.next()) rs.getInt(1) else null
        }
    }

    fun getMessagesByEvent(eventId: Int): List<EventMessage> = Database.useConnection { connection ->
        val sql = """
            SELECT m.id, m.event_id, m.user_id, m.message, m.created_at, m.updated_at,
                   u.user_name, u.user_surname
            FROM event_messages m
            JOIN users u ON m.user_id = u.id
            WHERE m.event_id = ?
            ORDER BY m.created_at ASC
        """.trimIndent()
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, eventId)
            val rs = stmt.executeQuery()
            rs.toList {
                EventMessage(
                    id = it.getInt("id"),
                    eventId = it.getInt("event_id"),
                    userId = it.getInt("user_id"),
                    userName = it.getString("user_name"),
                    userSurname = it.getString("user_surname"),
                    message = it.getString("message"),
                    createdAt = it.getTimestamp("created_at").toString(),
                    updatedAt = it.getTimestamp("updated_at").toString()
                )
            }
        }
    }
}

// DAO для проверки участия в событии
class ParticipantDao {
    fun isUserParticipant(eventId: Int, userId: Int): Boolean = Database.useConnection { connection ->
        val sql = "SELECT 1 FROM event_participants WHERE event_id = ? AND user_id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, eventId)
            stmt.setInt(2, userId)
            val rs = stmt.executeQuery()
            rs.next()
        }
    }

    fun addParticipant(eventId: Int, userId: Int, status: String = "accepted"): Boolean = Database.transaction { connection ->
        val sql = """
            INSERT INTO event_participants (event_id, user_id, status)
            VALUES (?, ?, ?)
            ON CONFLICT (event_id, user_id) DO NOTHING
        """.trimIndent()
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, eventId)
            stmt.setInt(2, userId)
            stmt.setString(3, status)
            stmt.executeUpdate() > 0
        }
    }

    fun removeParticipant(eventId: Int, userId: Int): Boolean = Database.transaction { connection ->
        val sql = "DELETE FROM event_participants WHERE event_id = ? AND user_id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, eventId)
            stmt.setInt(2, userId)
            stmt.executeUpdate() > 0
        }
    }

    fun getParticipantsByEvent(eventId: Int): List<Int> = Database.useConnection { connection ->
        val sql = "SELECT user_id FROM event_participants WHERE event_id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, eventId)
            val rs = stmt.executeQuery()
            rs.toList { it.getInt("user_id") }
        }
    }
}