package com.example.moveon.client.handlers

import com.example.moveon.client.api.EventsApi
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.CreateEventResponse
import com.example.moveon.client.jsonClasses.EventData
import com.example.moveon.client.jsonClasses.ViewEventResponse
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListRequest

class EventsHandler(private val api : EventsApi) {

    suspend fun getEvents (
        request : ViewFilteredEventsListRequest, page: Int, limit: Int): List<EventData> {

        val response = api.getFilteredEventsList(request, page, limit);

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response.events ?: emptyList()
    }

    suspend fun createEvent(request: CreateEventRequest): CreateEventResponse {
        val response = api.createEvent(request)

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response
    }

    suspend fun viewEvent(eventId : Int): ViewEventResponse {
        val response = api.viewEvent(eventId)

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response
    }
}
