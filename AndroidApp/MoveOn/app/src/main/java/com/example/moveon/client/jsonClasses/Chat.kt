package com.example.moveon.client.jsonClasses

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val eventId: Int,
    val message: String
)

@Serializable
data class SendMessageResponse(
    val success: Boolean,
    val messageId: Int? = null,
    val error: String? = null
)

@Serializable
data class EventMessage(
    val id: Int,
    val eventId: Int,
    val userId: Int,
    val userName: String? = null,
    val userSurname: String? = null,
    val message: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class GetMessagesResponse(
    val success: Boolean,
    val messages: List<EventMessage> = emptyList(),
    val error: String? = null
)