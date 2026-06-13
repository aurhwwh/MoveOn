package com.example.moveon.client.handlers

import com.example.moveon.client.api.ChatApi
import com.example.moveon.client.jsonClasses.EventMessage
import com.example.moveon.client.jsonClasses.SendMessageResponse

class ChatHandler {
    suspend fun sendMessage(eventId: Int, message: String): SendMessageResponse {
        return ChatApi.sendMessage(
            com.example.moveon.client.jsonClasses.SendMessageRequest(eventId, message)
        )
    }

    suspend fun getMessages(eventId: Int): List<EventMessage> {
        val response = ChatApi.getMessages(eventId)
        return if (response.success) response.messages else emptyList()
    }
}