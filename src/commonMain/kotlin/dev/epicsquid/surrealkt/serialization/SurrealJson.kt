package dev.epicsquid.surrealkt.serialization

import kotlinx.serialization.json.Json

val SurrealJson = Json {
    ignoreUnknownKeys = true
}