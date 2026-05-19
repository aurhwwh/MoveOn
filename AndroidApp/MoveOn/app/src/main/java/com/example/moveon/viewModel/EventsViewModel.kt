package com.example.moveon.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.moveon.client.handlers.EventsHandler
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.EventListElement
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime


class EventsPagingSource(
    private val handler: EventsHandler,
    private val filters: ViewFilteredEventsListRequest
) : PagingSource<Int, EventListElement>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventListElement> {
        val page = params.key ?: 0

        try {
            val events = handler.getEvents(filters, page, params.loadSize)

            return LoadResult.Page(
                data = events,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (events.size < params.loadSize) null else page + 1
            )
        } catch (e : Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, EventListElement>): Int? {
        return null
    }
}


class EventsViewModel : ViewModel() {
    private val handler = Handlers.eventsHandler

    @OptIn(ExperimentalTime::class)
    var filters by mutableStateOf(
        ViewFilteredEventsListRequest (
            title = null,
            city = "Saint-Petersburg",
            sportType = null,
            dateTime = null,
            maxAmountOfPeople = null,
            creatorRating = null
        )
    )
        private set

    @OptIn(ExperimentalTime::class)
    fun setCity(city: String) {
        filters = filters.copy(city = city)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventsFlow = snapshotFlow { filters }.flatMapLatest { newfilters ->
        Pager(
            config = PagingConfig(pageSize = 20,
                /* initialLoadSize = pageSize * 3 по дефолту (вроде),*/
                enablePlaceholders = false),
            pagingSourceFactory = {EventsPagingSource(handler, newfilters)}
        ).flow
    }.cachedIn(viewModelScope)


    var isCreating by mutableStateOf(false)
        private set

    var createSuccess by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            isCreating = true
            error = null
            createSuccess = false

            try {
                handler.createEvent(request)
                createSuccess = true
            } catch (e: Exception) {
                error = e.message
            } finally {
                isCreating = false
            }
        }
    }
}