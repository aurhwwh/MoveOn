package com.example.moveon.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveon.ui.common.BottomBar
import com.example.moveon.ui.common.CityTopBar
import com.example.moveon.viewModel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay


@Composable
fun MapScreen(navController : NavController,
              viewModel: MapViewModel = viewModel()) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        granted ->

        if (granted) {
            viewModel.onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val mapView = remember {

        MapView(context).apply {
            setTileSource(
                TileSourceFactory.MAPNIK
            )
            setMultiTouchControls(true)
            controller.setZoom(state.zoom)
            controller.setCenter(state.currentLocation)

            setUseDataConnection(true)

            addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {

                    event?.source?.mapCenter?.let {
                        viewModel.updateCenter(it as GeoPoint)
                    }

                    return true
                }

                override fun onZoom(event : ZoomEvent?) : Boolean {
                    event?.zoomLevel?.let {
                        viewModel.updateZoom(it)
                    }
                    return true
                }
            })
            val events = object : MapEventsReceiver {

                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {

                    if (p != null) {
                        viewModel.onMapClick(p)
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?) = false
            }

            overlays.add(MapEventsOverlay(events))
        }
    }

    LaunchedEffect(state.currentLocation) {
        mapView.controller.animateTo(state.currentLocation)
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, mapView) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val selectedMarker = remember { mutableStateOf<org.osmdroid.views.overlay.Marker?>(null) }
    LaunchedEffect(state.selectedPoint) {
        selectedMarker.value?.let {
            mapView.overlays.remove(it)
        }
        state.selectedPoint?.let { point ->

            val marker = org.osmdroid.views.overlay.Marker(mapView).apply {
                position = point
                setAnchor(
                    org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                    org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                )
                title = "Selected point"
            }

            selectedMarker.value = marker
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    LaunchedEffect(state.routes, state.builtRoute) {

        mapView.overlays.removeAll {
            it is org.osmdroid.views.overlay.Polyline
        }

        state.builtRoute.forEach { route ->

            val line = org.osmdroid.views.overlay.Polyline().apply {

                setPoints(route.points.map {
                    GeoPoint(it.lat, it.lon)
                })

                outlinePaint.color =
                    android.graphics.Color.rgb(10, 40, 120)

                outlinePaint.strokeWidth = 6f
                outlinePaint.alpha = 255
            }

            mapView.overlays.add(line)
        }

        state.routes.forEachIndexed { index, route ->

            val line = org.osmdroid.views.overlay.Polyline().apply {

                setPoints(route.points.map {
                    GeoPoint(it.lat, it.lon)
                })

                outlinePaint.color =
                    android.graphics.Color.BLUE

                outlinePaint.strokeWidth =
                    if (index == state.selectedRouteIndex) 18f else 14f

                outlinePaint.alpha =
                    if (index == state.selectedRouteIndex) 220 else 90

                setOnClickListener { _, _, _ ->
                    val projection = mapView.projection
                    val topLeft = projection.fromPixels(0, 0) as GeoPoint
                    val topRight = projection.fromPixels(mapView.width, 0) as GeoPoint
                    val bottomLeft = projection.fromPixels(0, mapView.height) as GeoPoint
                    val bottomRight = projection.fromPixels(mapView.width, mapView.height) as GeoPoint
                    val center = state.selectedPoint!!

                    val distances = listOf(
                        center.distanceToAsDouble(topLeft),
                        center.distanceToAsDouble(topRight),
                        center.distanceToAsDouble(bottomLeft),
                        center.distanceToAsDouble(bottomRight)
                    )

                    val radiusMeters = (distances.minOrNull()) ?: 100.0
                    viewModel.selectRoute(index,radiusMeters.toInt()/2)
                    true
                }
            }

            mapView.overlays.add(line)
        }

        mapView.invalidate()
    }


    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView }
            )

            if (state.showStartButton && state.selectedPoint != null) {

                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = {
                        val projection = mapView.projection
                        val topLeft = projection.fromPixels(0, 0) as GeoPoint
                        val topRight = projection.fromPixels(mapView.width, 0) as GeoPoint
                        val bottomLeft = projection.fromPixels(0, mapView.height) as GeoPoint
                        val bottomRight = projection.fromPixels(mapView.width, mapView.height) as GeoPoint
                        val center = state.selectedPoint!!

                        val distances = listOf(
                            center.distanceToAsDouble(topLeft),
                            center.distanceToAsDouble(topRight),
                            center.distanceToAsDouble(bottomLeft),
                            center.distanceToAsDouble(bottomRight)
                        )

                        val radiusMeters = (distances.minOrNull()) ?: 100.0
                        viewModel.startNewRoute(
                            state.selectedPoint!!.latitude,
                            state.selectedPoint!!.longitude,
                            radiusMeters.toInt()/2
                        )
                    }
                ) {
                    Text("Начать маршрут")
                }
            }
        }
    }
}