package dev.epicsquid.surrealkt.connection

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.Json

fun client() = HttpClient {
	install(WebSockets) {
		contentConverter = KotlinxWebsocketSerializationConverter(Json)
		pingInterval = 30
	}
}

suspend fun proofOfConcept() {
	client.webSocket(
		method = HttpMethod.Get,
		host = "127.0.0.1",
		port = 8000,
		path = "/sql"
	) {

	}
}