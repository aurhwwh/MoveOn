package com.example.moveon.client
import com.example.moveon.client.jsonClasses.RefreshRequest
import com.example.moveon.client.jsonClasses.RefreshResponse
import com.example.moveon.data.TokenStorage
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import com.example.moveon.client.api.RefreshApi

object Client {
    val client = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = TokenStorage.getAccess() ?: "",
                        refreshToken = TokenStorage.getRefresh() ?: ""
                    )
                }

                refreshTokens {
                    TokenStorage.getRefresh()?.let { refreshToken ->
                        try {
                            val response = RefreshApi.refresh(RefreshRequest(refreshToken))
                            if (response.newAccessToken.isNullOrBlank() || response.newRefreshToken.isNullOrBlank()){
                                println("Tokens are empty or blank!!!")
                                TokenStorage.clear()
                                null
                            }
                            else {
                                TokenStorage.saveTokens(
                                    response.newAccessToken,
                                    response.newRefreshToken
                                )
                                println("Tokens saved!!!")
                                BearerTokens(response.newAccessToken, response.newRefreshToken)
                            }
                        } catch (e: Exception) {
                            TokenStorage.clear()
                            println("Error: "+e.message)
                            null
                        }
                    }
                }
            }
        }
    }
}