package com.example.moveon.client.api

import com.example.moveon.client.Client.client
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File

object AvatarApi {
    suspend fun uploadAvatar(file: File): Boolean {
        return try {
            client.post("/api/user/avatar") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("avatar", file.readBytes(), Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                            })
                        }
                    )
                )
            }.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}