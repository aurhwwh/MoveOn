package com.example.moveon.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.App
import com.example.moveon.ui.map.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

data class MapUiState(
    val currentLocation: GeoPoint = GeoPoint(59.9386, 30.2144),
    val permissionGranted: Boolean = false,
    val isLoading: Boolean = false,
    val zoom: Double = 19.0
)


class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val userLocation = UserLocation(application)

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()


    private fun loadLocation() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val currentLocation = userLocation.getCurrentLocation()

            if(currentLocation != null) {
                _state.update {
                    it.copy(currentLocation = currentLocation, isLoading = false)
                }
            }
            else {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }


    fun onPermissionGranted() {
        _state.update { it.copy(permissionGranted = true) }
        loadLocation()
    }

    fun updateZoom(zoom: Double) {
        _state.update {
            it.copy(zoom = zoom)
        }
    }

    fun updateCenter(center: GeoPoint) {
        _state.update {
            it.copy(currentLocation = center)
        }
    }
}

