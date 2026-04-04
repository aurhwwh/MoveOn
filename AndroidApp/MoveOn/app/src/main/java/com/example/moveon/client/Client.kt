package com.example.moveon.client
import com.example.moveon.DateSerializer
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDate

object Client {
    val client = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    serializersModule = SerializersModule {
                        contextual(LocalDate::class, DateSerializer)
                    }
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }
}