package com.example.moveon.client.handlers

import com.example.moveon.client.api.EventsApi
import com.example.moveon.client.jsonClasses.CreateEventRequest
import com.example.moveon.client.jsonClasses.CreateEventWithRouteRequest
import com.example.moveon.client.jsonClasses.EventListElement
import com.example.moveon.client.jsonClasses.EventsMarker
import com.example.moveon.client.jsonClasses.JoinApplicationRequest
import com.example.moveon.client.jsonClasses.RateRequest
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

    suspend fun createEventWithRoute(request: CreateEventWithRouteRequest) {
        val response = api.createEventWithRoute(request)

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

    suspend fun getMyEvents (type : String): List<EventListElement> {

        val response = api.getMyEventsList(type)

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response.events ?: emptyList()
    }

    suspend fun getMarkers (
        minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<EventsMarker> {

        val response = api.getEventsMarkers(minLat, maxLat, minLon, maxLon);

        if (!response.success) {
            throw Exception(response.errorMessage ?: "UnknownError");
        }

        return response.events ?: emptyList()
    }

    suspend fun rateUser (request: RateRequest) {
        val response = api.rateUser(request)

        if (!response.success) {
            throw Exception(response.errorMessage ?: "Unknown error")
        }
    }
}
