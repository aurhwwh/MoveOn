package com.example.moveon.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.handlers.Place
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GeocodingViewModel : ViewModel() {
    private val handler = Handlers.geocodingHandler

    var suggestion by mutableStateOf<List<Place>>(emptyList())
        private set


    private var job : Job? = null

    fun onQueryChanged(query : String) {
        job?.cancel()

        if (query.length < 4) {
            suggestion = emptyList()
            return
        }

        job = viewModelScope.launch {
            delay(400)

            suggestion = try {
                handler.searchPlace(query)
            } catch (e : Exception) {
                emptyList()
            }
        }
    }
}