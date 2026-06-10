package com.example.moveon.viewModel

import android.R
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.EventsMarker
import com.example.moveon.client.jsonClasses.Point
import com.example.moveon.client.jsonClasses.Route
import com.example.moveon.ui.map.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

data class MapUiState(
    val userLocation: GeoPoint? = null,
    val mapCenter: GeoPoint = GeoPoint(59.9386, 30.2144),
    val permissionGranted: Boolean = false,
    val isLoading: Boolean = false,
    val zoom: Double = 19.0,
    val selectedPoint: GeoPoint? = null,
    val showStartButton: Boolean = false,
    val routes: List<Route> = emptyList(),
    val selectedRouteIndex: Int? = null,
    val builtRoute: List<Route> = emptyList(),
    val markers: List<EventsMarker> = emptyList(),
    val showedEventRoute:List<Point> = emptyList(),
    val showEventRoute: Boolean = false
)


class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val userLocation = UserLocation(application)

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()


    fun loadMarkers(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ) {
        viewModelScope.launch {
            try {
                val markers = Handlers.eventsHandler.getMarkers(minLat, maxLat, minLon, maxLon)

                _state.update { it.copy(markers = markers) }

            } catch (e: Exception) {
                Log.e("MapViewModel", "Failed to load markers", e)
            }
        }
    }

    fun startNewRoute(lat: Double, lon: Double, radius: Int) {

        _state.update {
            it.copy(
                builtRoute = emptyList(),
                routes = emptyList(),
                selectedRouteIndex = null
            )
        }

        loadRouteOptions(lat, lon, radius)

    }

    fun loadRouteOptions(lat: Double, lon: Double, radius : Int) {
        viewModelScope.launch {

            val response = Handlers.mapRoutesHandler
                .getRouteOptions(lat, lon, radius)

            _state.update {
                it.copy(
                    showStartButton = false,
                    routes = response.routes ?: emptyList(),
                    selectedRouteIndex = null,
                    selectedPoint = GeoPoint(response.centralPoint?.lat ?: lat,response.centralPoint?.lon ?: lon)
                )
            }
        }
    }

    fun createEventWithRoute() {

    }

    fun selectRoute(index: Int, radius: Int) {

        val route = state.value.routes[index]
        val lastPoint = route.points.last()

        _state.update {
            it.copy(
                selectedRouteIndex = index,
                builtRoute = it.builtRoute + route,

                routes = emptyList()
            )
        }

        loadRouteOptions(lastPoint.lat, lastPoint.lon, radius)
    }

    fun undoStep(radius: Int) {

        val current = state.value.builtRoute

        if (current.isEmpty()) return

        val newBuilt = current.dropLast(1)

        _state.update {
            it.copy(
                builtRoute = newBuilt,
                routes = emptyList(),
                selectedRouteIndex = null
            )
        }

        if (newBuilt.isEmpty()) {
            state.value.selectedPoint?.let {
                loadRouteOptions(
                    it.latitude,
                    it.longitude,
                    radius
                )
            }
        } else {
            val lastPoint = newBuilt.last().points.last()

            loadRouteOptions(
                lastPoint.lat,
                lastPoint.lon,
                radius
            )
        }
    }


    private fun loadLocation() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val currentLocation = userLocation.getCurrentLocation()

            if(currentLocation != null) {
                _state.update {
                    it.copy(
                        userLocation = currentLocation,
                        mapCenter = currentLocation,
                        isLoading = false
                    )
                }
            }
            else {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun onMapClick(point: GeoPoint) {
        _state.update {
            it.copy(
                selectedPoint = point,
                showStartButton = true
            )
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
            it.copy(mapCenter = center)
        }
    }

    private val handler = Handlers.eventsHandler
    fun loadEvent(eventId: Int) {
        viewModelScope.launch {

            try {
                val event = handler.viewEvent(eventId)
                _state.update{
                    it.copy(showedEventRoute = event.route!!,
                        showEventRoute = true
                        )
                }
            } catch (e: Exception) {
               println("Error loading route event")
            }
        }
    }
}

