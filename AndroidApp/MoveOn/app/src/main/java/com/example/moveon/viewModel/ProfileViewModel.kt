package com.example.moveon.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.data.ProfileData
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val handler = Handlers.profileHandler

    var profile by mutableStateOf<ProfileData?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadProfile(profileId: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                profile = handler.getProfile(profileId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}