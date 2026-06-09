package com.example.moveon.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.EditProfileRequest
import com.example.moveon.client.jsonClasses.EventListElement
import com.example.moveon.data.ProfileData
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ProfileViewModel : ViewModel() {

    private val handler = Handlers.profileHandler
    private val eventsHandler = Handlers.eventsHandler

    var profile by mutableStateOf<ProfileData?>(null)
        private set

    var isProfileLoading by mutableStateOf(true)
        private set

    var profileError by mutableStateOf<String?>(null)
        private set

    fun loadMyProfile() {
        if (profile != null) {
            return
        }

        viewModelScope.launch {
            isProfileLoading = true
            profileError = null

            try {
                profile = handler.getMyProfile()
            } catch (e: Exception) {
                profileError = e.message
            } finally {
                isProfileLoading = false
            }
        }
    }

    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            isProfileLoading = true
            profileError = null

            try {
                profile = handler.getUserProfile(userId)
            } catch (e: Exception) {
                profileError = e.message
            } finally {
                isProfileLoading = false
            }
        }
    }


    var myEvents by mutableStateOf<List<EventListElement>>(emptyList())
        private set

    var selectedEventsType by mutableStateOf("All")
        private set
    var isEventsLoading by mutableStateOf(false)
        private set

    var eventsError by mutableStateOf<String?>(null)
        private set

    fun updateSelectedEventsType(type: String) {
        selectedEventsType = type
    }

    val filteredEvents: List<EventListElement>
        get() = when (selectedEventsType) {
            "Created by me" -> myEvents.filter { it.isCreator }
            else -> myEvents
        }

    fun loadMyEvents() {
        if (myEvents.isNotEmpty()) {
            return
        }

        viewModelScope.launch {
            isEventsLoading = true
            eventsError = null

            try {
                myEvents = eventsHandler.getMyEvents()
            } catch (e: Exception) {
                eventsError = e.message
            } finally {
                isEventsLoading = false
            }
        }
    }


    var editSuccess by mutableStateOf(false)
        private set

    var editError by mutableStateOf<String?>(null)
        private set

    var isEditing by mutableStateOf(false)
        private set

    fun editProfile(
        name: String,
        surname: String,
        birth: LocalDate,
        description: String
    ) {
        viewModelScope.launch {
            Log.d("PROFILE_EDIT", "Start editing")

            isEditing = true
            editError = null
            editSuccess = false

            try {
                val request = EditProfileRequest(
                    userName = name,
                    userSurname = surname,
                    dateOfBirth = birth,
                    description = description
                )

                handler.editProfile(request)

                profile = profile?.copy(
                    name = name,
                    surname = surname,
                    birth = birth,
                    description = description
                )

                Log.d("PROFILE_EDIT", "Success")
                editSuccess = true

            } catch (e: Exception) {
                editError = e.message
                Log.e("PROFILE_EDIT", "Error", e)

            } finally {
                isEditing = false
            }
        }
    }

    fun clearEditState() {
        editSuccess = false
        editError = null
    }
}