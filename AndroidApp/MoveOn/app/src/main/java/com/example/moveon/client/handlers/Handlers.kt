package com.example.moveon.client.handlers

import com.example.moveon.client.Client
import com.example.moveon.client.api.ProfileApi

object Handlers {
    val profileHandler = ProfileHandler(ProfileApi(Client.client))
}