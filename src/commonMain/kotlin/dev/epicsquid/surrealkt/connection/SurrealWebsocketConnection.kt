package dev.epicsquid.surrealkt.connection

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class SurrealWebsocketConnection(
	private val host: String,
	private val port: Int = 8000,
	private val client: HttpClient = client()
) {

	/**
	 * Defines the last request made to the DB for querying
	 */
	private val lastRequestId: Long = 0

	/**
	 * Determines if the current connection loop should finish
	 */
	private var connected = false

	suspend fun connect() {
		if (!connected) {
			connected = true
			client.webSocket(method = HttpMethod.Get, host = host, port = port, path = "/rpc") {
				val messageOutputRoutine = launch { outputMessages() }
				val userInputRoutine = launch { inputMessages() }

				userInputRoutine.join() // Wait for completion; either "exit" or error
				messageOutputRoutine.cancelAndJoin()
			}
		}
	}

	fun disconnect() {
		connected = false
	}

	suspend fun DefaultClientWebSocketSession.outputMessages() {
		try {
			for (message in incoming) {
				message as? Frame.Text ?: continue
				println(message.readText())
			}
		} catch (e: Exception) {
//			println("Error while receiving: " + e.localizedMessage)
		}
	}

	suspend fun DefaultClientWebSocketSession.inputMessages() {
		while (true) {
			val message = readln() ?: ""
			if (message.equals("exit", true)) return
			try {
				send(message)
			} catch (e: Exception) {
//				println("Error while sending: " + e.localizedMessage)
				return
			}
		}
	}
}