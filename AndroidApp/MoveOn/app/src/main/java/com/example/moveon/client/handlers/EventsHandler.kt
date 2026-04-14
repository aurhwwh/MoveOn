package com.example.moveon.client.handlers

import com.example.moveon.client.api.EventsApi
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.EventListElement
import com.example.moveon.client.jsonClasses.JoinApplicationRequest
import com.example.moveon.client.jsonClasses.ViewEventResponse
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListRequest


class EventsHandler(private val api : EventsApi) {

    suspend fun getEvents (
        request : ViewFilteredEventsListRequest, page: Int, limit: Int): List<EventListElement> {

        val response = api.getFilteredEventsList(request, page, limit);

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response.events ?: emptyList()
    }

    suspend fun createEvent(request: CreateEventRequest) {
        val response = api.createEvent(request)

        if (!response.success) {
            throw Exception(response.errorMessage ?: "Unknown error")
        }
    }

    suspend fun viewEvent(eventId : Int): ViewEventResponse {
        val response = api.viewEvent(eventId)

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response
    }

    suspend fun joinApplication(eventId: Int) {
        val response = api.joinApplication(JoinApplicationRequest(eventId))

        if (!response.success) {
            throw Exception(response.errorMessage ?: "Unknown error")
        }
    }
}
