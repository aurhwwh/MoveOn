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

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                event = handler.viewEvent(eventId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}