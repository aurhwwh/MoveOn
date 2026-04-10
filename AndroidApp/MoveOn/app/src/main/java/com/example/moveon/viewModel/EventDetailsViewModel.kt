package com.example.moveon.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.ViewEventResponse
import kotlinx.coroutines.launch


class EventDetailsViewModel : ViewModel() {

    private val handler = Handlers.eventsHandler

    var event by mutableStateOf<ViewEventResponse?>(null)
        private set

    var isLoadingEvent by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            isLoadingEvent = true
            error = null

            try {
                event = handler.viewEvent(eventId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoadingEvent = false
            }
        }
    }


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
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoadingEvent = false
            }
        }
    }
}