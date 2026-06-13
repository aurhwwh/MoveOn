package com.example.moveon.client.api
import io.ktor.client.call.body
import com.example.moveon.client.Client
import com.example.moveon.client.jsonClasses.GetMessagesResponse
import com.example.moveon.client.jsonClasses.SendMessageRequest
import com.example.moveon.client.jsonClasses.SendMessageResponse
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

object ChatApi {
    private val client = Client.client
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun sendMessage(request: SendMessageRequest): SendMessageResponse {
        return client.post {
            url("$baseUrl/send_message")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getMessages(eventId: Int): GetMessagesResponse {
        return client.get {
            url("$baseUrl/messages/$eventId")
        }.body()
    }
}