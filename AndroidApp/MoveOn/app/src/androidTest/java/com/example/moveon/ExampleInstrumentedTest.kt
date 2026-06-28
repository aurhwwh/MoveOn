package com.example.moveon

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moveon.viewModel.MapViewModel

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.osmdroid.util.GeoPoint

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MapViewModelTest {

    private fun createViewModel(): MapViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        return MapViewModel(app)
    }

    @Test
    fun onMapClick_selectsPointAndShowsButton() {

        val vm = createViewModel()

        val point = GeoPoint(10.0, 20.0)
        vm.onMapClick(point)

        assertEquals(
            point,
            vm.state.value.selectedPoint
        )
        assertTrue(
            vm.state.value.showStartButton
        )
    }

    @Test
    fun updateZoom_changesZoom() {

        val vm = createViewModel()

        vm.updateZoom(15.0)

        assertEquals(
            15.0,
            vm.state.value.zoom,
            0.0
        )
    }

    @Test
    fun updateCenter_changesMapCenter() {

        val vm = createViewModel()

        val point = GeoPoint(50.0, 40.0)
        vm.updateCenter(point)

        assertEquals(
            point,
            vm.state.value.mapCenter
        )
    }

    @Test
    fun permissionGranted_changesState() {

        val vm = createViewModel()

        vm.onPermissionGranted()

        assertTrue(
            vm.state.value.permissionGranted
        )
    }
}
