package com.example.moveon.client.handlers

import com.example.moveon.client.Client
import com.example.moveon.client.api.EntryApi
import com.example.moveon.client.api.EventsApi
import com.example.moveon.client.api.GeocodingApi
import com.example.moveon.client.api.MapRoutesApi
import com.example.moveon.client.api.ProfileApi

object Handlers {
    val profileHandler = ProfileHandler(ProfileApi(Client.client))
    val eventsHandler = EventsHandler(EventsApi(Client.client))
    val entryHandler = EntryHandler(EntryApi(Client.client))
    val geocodingHandler = GeocodingHandler(GeocodingApi(Client.client))
    val mapRoutesHandler = MapRoutesHandler(MapRoutesApi(Client.client))

    val chatHandler = ChatHandler()
}