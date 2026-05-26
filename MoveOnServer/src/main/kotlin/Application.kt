package MoveOn

import MoveOn.database.Database
import io.ktor.server.application.*
import java.io.File

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    GraphHopperProvider.hopper
    Database
    configureSerialization()
    configureSecurity()

    val avatarDir = File(System.getProperty("user.dir"), "avatars")
    AvatarService.init(avatarDir)

    configureRouting()
}