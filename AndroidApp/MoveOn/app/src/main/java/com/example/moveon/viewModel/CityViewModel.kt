package com.example.moveon.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CityViewModel : ViewModel() {

    private val _selectedCity = MutableStateFlow("Saint-Petersburg")
    val selectedCity: StateFlow<String> = _selectedCity

    fun updateCity(city: String) {
        _selectedCity.value = city
    }
}