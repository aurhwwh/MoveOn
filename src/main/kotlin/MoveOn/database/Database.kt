package MoveOn.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

object Database {
    private val dataSource: HikariDataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/movedb"
            username = "postgres"
            password = "seva1234"
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

    inline fun <reified T : Any> ResultSet.toObject(transform: (ResultSet) -> T): T? =
        if (next()) transform(this) else null

    inline fun <reified T : Any> ResultSet.toList(transform: (ResultSet) -> T): List<T> {
        val list = mutableListOf<T>()
        while (next()) {
            list.add(transform(this))
        }
        return list
    }
}