package com.example.moveon.client.api

import com.example.moveon.client.Client.client
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListRequest
import com.example.moveon.client.jsonClasses.ViewFilteredEventsListResponse
import com.example.moveon.client.jsonClasses.ViewProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.request

class EventsApi(private val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getFilteredEventsList(
        request : ViewFilteredEventsListRequest, page: Int, limit: Int
    ): ViewFilteredEventsListResponse {

        return client.get("$baseUrl/view_filtered_events_list") {
            parameter("page", page)
            parameter("limit", limit)
            request.title?.let{parameter("title", it)}
            request.city?.let { parameter("city", it)}
            request.sportType?.let{parameter("sportType", it)}
            request.date?.let { parameter("date", it)}
            request.maxAmountOfPeople?.let{parameter("maxAmountOfPeople", it)}
            request.creatorRating?.let { parameter("creatorRating", it)}
        }.body()
    }
}