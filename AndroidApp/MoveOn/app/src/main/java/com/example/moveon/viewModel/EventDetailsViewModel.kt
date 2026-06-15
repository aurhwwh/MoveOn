package com.example.moveon.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.EventMessage
import com.example.moveon.client.jsonClasses.RateRequest
import com.example.moveon.client.jsonClasses.UserRating
import com.example.moveon.client.jsonClasses.ViewEventResponse
import kotlinx.coroutines.launch

class EventDetailsViewModel : ViewModel() {

    private val handler = Handlers.eventsHandler
    private val chatHandler = Handlers.chatHandler

    var event by mutableStateOf<ViewEventResponse?>(null)
        private set

    var isLoadingEvent by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var messages by mutableStateOf<List<EventMessage>>(emptyList())
        private set

    var isLoadingMessages by mutableStateOf(false)
        private set

    var isSendingMessage by mutableStateOf(false)
        private set

    var sendMessageError by mutableStateOf<String?>(null)
        private set

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            isLoadingEvent = true
            error = null

            try {
                event = handler.viewEvent(eventId)
                if (event?.isUserParticipant == true) {
                    loadMessages(eventId)
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoadingEvent = false
            }
        }
    }

    private fun loadMessages(eventId: Int) {
        viewModelScope.launch {
            isLoadingMessages = true
            try {
                messages = chatHandler.getMessages(eventId)
            } catch (e: Exception) {
                // обработать ошибку загрузки сообщений
            } finally {
                isLoadingMessages = false
            }
        }
    }

    fun sendMessage(eventId: Int, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            isSendingMessage = true
            sendMessageError = null
            try {
                val response = chatHandler.sendMessage(eventId, text)
                if (response.success) {
                    loadMessages(eventId)
                } else {
                    sendMessageError = response.error ?: "Failed to send"
                }
            } catch (e: Exception) {
                sendMessageError = e.message
            } finally {
                isSendingMessage = false
            }
        }
    }

    fun clearSendMessageError() { sendMessageError = null }

    var isJoining by mutableStateOf(false)
        private set

    var joinSuccess by mutableStateOf(false)
        private set

    fun joinEvent(eventId: Int) {
        viewModelScope.launch {
            isJoining = true
            error = null
            joinSuccess = false
            try {
                handler.joinApplication(eventId)
                joinSuccess = true
                loadEvent(eventId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isJoining = false
            }
        }
    }


    var isRating by mutableStateOf(false)
        private set

    var rateError by mutableStateOf<String?>(null)
        private set

    var showRating by mutableStateOf(false)
        private set

    fun openRating() {
        showRating = true
    }

    fun closeRating() {
        showRating = false
    }

    fun rateEvent(eventId: Int, ratings: Map<Int, Int>) {
        println("rateEvent called")
        viewModelScope.launch {
            isRating = true
            rateError = null

            try {
                handler.rateUser(
                    RateRequest(
                        eventId = eventId,
                        ratings = ratings.filterValues { it > 0 }.map {
                            UserRating(
                                ratedUserId = it.key,
                                rating = it.value.toDouble()
                            )
                        }
                    )
                )

                loadEvent(eventId)
                showRating = false

            } catch (e: Exception) {
                rateError = e.message
            } finally {
                isRating = false
            }
        }
    }
}