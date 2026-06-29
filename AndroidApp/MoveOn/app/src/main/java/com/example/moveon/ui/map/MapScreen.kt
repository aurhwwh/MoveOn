package com.example.moveon.ui.map

import android.Manifest
import com.example.moveon.R
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
import com.example.moveon.viewModel.MapViewModel
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.moveon.client.jsonClasses.CreateEventWithRouteRequest
import com.example.moveon.client.jsonClasses.EventsMarker
import com.example.moveon.ui.events.CreateEvent
import com.example.moveon.ui.theme.MGreen
import kotlinx.coroutines.delay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController : NavController,
              viewModel: MapViewModel = viewModel(),
              eventId: Int? = null
              ) {
    LaunchedEffect(eventId) {
        if(eventId!=null)
        viewModel.loadEvent(eventId)
    }

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
            controller.setCenter(state.mapCenter)

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

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    if (p != null) {
                        viewModel.startNewRoute(p.latitude,p.longitude,this@apply.visibleRadius(p))
                    }
                    return true
                }
            }

            overlays.add(MapEventsOverlay(events))
        }

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
            mapView.overlays.clear()
            mapView.onDetach()
        }
    }

    val selectedMarker = remember { mutableStateOf<org.osmdroid.views.overlay.Marker?>(null) }
    val routeOverlays = remember { mutableListOf<org.osmdroid.views.overlay.Polyline>() }
    val markerOverlays = remember { mutableListOf<org.osmdroid.views.overlay.Marker>() }
    var selectedEvent by remember { mutableStateOf<EventsMarker?>(null) }
    var showCreateEventRoute by remember { mutableStateOf(false) }
    var showCreateEventPoint by remember { mutableStateOf(false) }

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

        routeOverlays.forEach {
            mapView.overlays.remove(it)
        }

        routeOverlays.clear()

        state.builtRoute.forEach { route ->

            val line = org.osmdroid.views.overlay.Polyline().apply {

                setPoints(route.points.map {
                    GeoPoint(it.lat, it.lon)
                })

                outlinePaint.color =
                    android.graphics.Color.rgb(10, 40, 120)

                outlinePaint.strokeWidth = 8f
                outlinePaint.alpha = 255
                outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
            }

            mapView.overlays.add(line)
            routeOverlays.add(line)
        }

        state.routes.forEachIndexed { index, route ->

            val line = org.osmdroid.views.overlay.Polyline().apply {

                setPoints(route.points.map {
                    GeoPoint(it.lat, it.lon)
                })

                outlinePaint.color =
                    android.graphics.Color.rgb(138, 43, 226)

                outlinePaint.color = android.graphics.Color.rgb(138, 43, 226)

                outlinePaint.strokeWidth =
                    if (index == state.selectedRouteIndex) 25f else 20f

                outlinePaint.alpha =
                    if (index == state.selectedRouteIndex) 120 else 90
                outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND


                setOnClickListener { _, _, _ ->
                    viewModel.selectRoute(
                        index,
                        mapView.visibleRadius(state.selectedPoint!!)
                    )
                    true
                }
            }

            mapView.overlays.add(line)
            routeOverlays.add(line)
        }

        //mapView.invalidate()
    }

    LaunchedEffect(state.showedEventRoute) {

        if (!state.showEventRoute) return@LaunchedEffect

        routeOverlays.forEach { mapView.overlays.remove(it) }
        routeOverlays.clear()

        val geoPoints = state.showedEventRoute.map {
            GeoPoint(it.lat, it.lon)
        }

        val line = org.osmdroid.views.overlay.Polyline().apply {
            setPoints(geoPoints)
            outlinePaint.color =  android.graphics.Color.rgb(138, 43, 226)
            outlinePaint.strokeWidth = 12f
            outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
            outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
        }

        mapView.overlays.add(line)
        routeOverlays.add(line)

        if (geoPoints.isNotEmpty()) {
            val boundingBox = org.osmdroid.util.BoundingBox.fromGeoPoints(geoPoints)
            mapView.zoomToBoundingBox(
                boundingBox,
                false,
                200
            )
        }
    }


    LaunchedEffect(state.mapCenter, state.zoom) {

        delay(400)

        val projection = mapView.projection

        val topLeft = projection.fromPixels(0, 0) as GeoPoint
        val bottomRight = projection.fromPixels(mapView.width, mapView.height) as GeoPoint

        val minLat = bottomRight.latitude
        val maxLat = topLeft.latitude

        val minLon = topLeft.longitude
        val maxLon = bottomRight.longitude

        viewModel.loadMarkers(minLat, maxLat, minLon, maxLon)
    }

    LaunchedEffect(state.markers) {

        markerOverlays.forEach { mapView.overlays.remove(it)    }
        markerOverlays.clear()

        state.markers.forEach { event ->
            val marker =
                org.osmdroid.views.overlay.Marker(mapView).apply {

                    position = GeoPoint(
                        event.lat,
                        event.lon
                    )

                    title = event.title

                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.event_marker
                    )

                    setAnchor(
                        org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                        org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                    )

                    setOnMarkerClickListener { _, _ ->
                        selectedEvent = event
                        true
                    }
                }

            mapView.overlays.add(marker)
            markerOverlays.add(marker)
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
                .statusBarsPadding()
        ) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView }
            )

            if (state.builtRoute.size >=2) {
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    onClick = {
                        val p = state.builtRoute.getOrNull(state.builtRoute.size - 2)?.points?.lastOrNull()
                        if(p!=null) {
                            viewModel.undoStep(
                                mapView.visibleRadius(GeoPoint(p.lat,p.lon))
                            )
                        }
                    }
                ) {
                    Text("Отменить")
                }
            }

            if (state.showStartButton && state.selectedPoint != null || !state.builtRoute.isEmpty()) {
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = {
                        if (state.builtRoute.isEmpty()){
                            showCreateEventPoint = true
                        }
                        else {
                            showCreateEventRoute = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MGreen)
                ) {
                    Text("Создать событие",fontSize = 12.sp)
                }
            }
            if (showCreateEventPoint) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showCreateEventPoint = false
                    }
                ) {
                    val point = state.selectedPoint
                    if (point != null) {
                        CreateEvent(
                            navController = navController,
                            lat = point.latitude,
                            lon = point.longitude,
                        )
                    }
                }
            }
            if (showCreateEventRoute) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showCreateEventRoute = false
                    }
                ) {
                    val firstPoint = state.builtRoute
                        .firstOrNull()
                        ?.points
                        ?.firstOrNull()
                    if (firstPoint != null) {
                        CreateEvent(
                            navController = navController,
                            lat = firstPoint.lat,
                            lon = firstPoint.lon,
                            route = state.builtRoute.flatMap { it.points }
                        )
                    }
                }
            }

            selectedEvent?.let { event ->

                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    MapEventCard(
                        navController,
                        event = event,
                        onDismiss = {
                            selectedEvent = null
                        }
                    )
                }
            }
        }
    }
}
private fun MapView.visibleRadius(center: GeoPoint): Int {

    val projection = projection

    val topLeft = projection.fromPixels(0, 0) as GeoPoint
    val topRight = projection.fromPixels(width, 0) as GeoPoint
    val bottomLeft = projection.fromPixels(0, height) as GeoPoint
    val bottomRight = projection.fromPixels(width, height) as GeoPoint

    val radius = listOf(
        center.distanceToAsDouble(topLeft),
        center.distanceToAsDouble(topRight),
        center.distanceToAsDouble(bottomLeft),
        center.distanceToAsDouble(bottomRight)
    ).minOrNull() ?: 100.0

    return radius.toInt() / 2
}