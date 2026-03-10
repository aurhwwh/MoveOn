package MoveOn

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import MoveOn.database.Database

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    Database
    configureSerialization()
    configureRouting()
}