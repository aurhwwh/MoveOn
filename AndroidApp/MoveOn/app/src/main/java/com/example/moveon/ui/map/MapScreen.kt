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


    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        innerPadding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(innerPadding),

            factory = {mapView}
        )
    }
}