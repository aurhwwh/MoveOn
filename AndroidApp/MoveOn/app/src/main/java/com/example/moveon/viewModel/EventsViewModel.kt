package com.example.moveon.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.moveon.client.handlers.EventsHandler
import com.example.moveon.client.handlers.Handlers
import com.example.moveon.client.jsonClasses.EventData
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListRequest


class EventsPagingSource(
    private val handler: EventsHandler,
    private val filters: ViewFilteredEventsListRequest
) : PagingSource<Int, EventData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventData> {
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

    override fun getRefreshKey(state: PagingState<Int, EventData>): Int? {
        return null
    }
}


class EventsViewModel () : ViewModel() {
    private val handler = Handlers.eventsHandler
    private val defaultFilters = ViewFilteredEventsListRequest (
        title = null,
        city = null,
        sportType = null,
        date = null,
        maxAmountOfPeople = null,
        creatorRating = null
    )

    val eventsFlow = Pager(
        config = PagingConfig(pageSize = 20,
            /* initialLoadSize = pageSize * 3 по дефолту (вроде),*/
            enablePlaceholders = false),
        pagingSourceFactory = {EventsPagingSource(handler, defaultFilters)}
    ).flow.cachedIn(viewModelScope)
}