package com.example.moveon

import com.example.moveon.viewModel.CityViewModel
import com.example.moveon.viewModel.EventDetailsViewModel
import com.example.moveon.viewModel.EventOwnerFilter
import com.example.moveon.viewModel.EventTimeFilter
import com.example.moveon.viewModel.EventsViewModel
import com.example.moveon.viewModel.GeocodingViewModel
import com.example.moveon.viewModel.ProfileViewModel
import org.junit.Test

import org.junit.Assert.*
import org.osmdroid.util.GeoPoint

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class CityViewModelTest {
    @Test
    fun updateCity_changesSelectedCity() {
        val vm = CityViewModel()

        vm.updateCity("Москва")

        assertEquals("Москва", vm.selectedCity.value)
    }
}

class EventsViewModelTest {
    @Test
    fun updateFilters_updatesAllFields() {
        val vm = EventsViewModel()

        vm.updateFilters(
            sportType = "Футбол",
            nextDays = 5,
            maxAmountOfPeople = 10,
            creatorRating = 4.5
        )

        assertEquals("Футбол", vm.filters.sportType)
        assertEquals(5, vm.filters.nextDays)
        assertEquals(10, vm.filters.maxAmountOfPeople)
        assertEquals(4.5, vm.filters.creatorRating)
    }

    @Test
    fun clearFilters_resetsAllFilters() {
        val vm = EventsViewModel()

        vm.updateFilters(
            "Бег",
            3,
            20,
            4.8
        )

        vm.clearFilters()

        assertNull(vm.filters.sportType)
        assertNull(vm.filters.nextDays)
        assertNull(vm.filters.maxAmountOfPeople)
        assertNull(vm.filters.creatorRating)
    }

    @Test
    fun setCity_changesCity() {
        val vm = EventsViewModel()

        vm.setCity("Москва")

        assertEquals("Москва", vm.filters.city)
    }
}

class ProfileViewModelTest {
    @Test
    fun defaultFilters_areCorrect() {
        val vm = ProfileViewModel()

        assertEquals(EventTimeFilter.UPCOMING, vm.selectedTimeFilter)
        assertEquals(EventOwnerFilter.ANYONE, vm.selectedOwnerFilter)
    }

    @Test
    fun updateOwnerFilter_changesFilter() {
        val vm = ProfileViewModel()

        vm.updateOwnerFilter(EventOwnerFilter.ME)

        assertEquals(EventOwnerFilter.ME, vm.selectedOwnerFilter)
    }

    @Test
    fun clearEditState_resetsEditStatus() {
        val vm = ProfileViewModel()

        vm.clearEditState()

        assertFalse(vm.editSuccess)
        assertNull(vm.editError)
    }
}

class EventDetailsViewModelTest {
    @Test
    fun openRating_showsRatingDialog() {
        val vm = EventDetailsViewModel()

        vm.openRating()

        assertTrue(vm.showRating)
    }


    @Test
    fun closeRating_hidesRatingDialog() {
        val vm = EventDetailsViewModel()

        vm.openRating()
        vm.closeRating()

        assertFalse(vm.showRating)
    }

    @Test
    fun clearSendMessageError_removesError() {
        val vm = EventDetailsViewModel()

        vm.clearSendMessageError()

        assertNull(vm.sendMessageError)
    }

    @Test
    fun defaultState_isCorrect() {
        val vm = EventDetailsViewModel()

        assertNull(vm.event)
        assertTrue(vm.isLoadingEvent)
        assertNull(vm.error)

        assertTrue(vm.messages.isEmpty())
        assertFalse(vm.showRating)
        assertFalse(vm.isJoining)
        assertFalse(vm.isRating)
    }

    @Test
    fun sendMessage_withBlankText_doesNothing() {
        val vm = EventDetailsViewModel()

        vm.sendMessage(1, "   ")

        assertFalse(vm.isSendingMessage)
        assertNull(vm.sendMessageError)
    }
}


class GeocodingViewModelTest {

    @Test
    fun shortQuery_noSuggestions() {
        val vm = GeocodingViewModel()

        vm.onQueryChanged("abc")

        assertTrue(vm.suggestion.isEmpty())
    }

    @Test
    fun reverseResult_initiallyNull() {
        val vm = GeocodingViewModel()

        assertNull(vm.reverseResult)
    }
}