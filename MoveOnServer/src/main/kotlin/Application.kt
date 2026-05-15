package MoveOn

import MoveOn.database.Database
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    GraphHopperProvider.hopper
    Database
    configureSerialization()
    configureSecurity()
    configureRouting()
}
