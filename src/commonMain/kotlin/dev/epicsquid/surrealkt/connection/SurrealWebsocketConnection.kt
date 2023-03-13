package dev.epicsquid.surrealkt.connection

import dev.epicsquid.surrealkt.connection.model.RpcRequest
import dev.epicsquid.surrealkt.connection.model.RpcResponse
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SurrealWebsocketConnection(
	private val host: String,
	private val port: Int = 8000,
	private val client: HttpClient = client()
) {
	private val _responses: MutableSharedFlow<RpcResponse> = MutableSharedFlow()
	val responses: SharedFlow<RpcResponse> = _responses.asSharedFlow()

	suspend fun connect(inputs: Flow<RpcRequest<*>>) {
		client.webSocket(method = HttpMethod.Get, host = host, port = port, path = "/rpc") {
			val responseMessageRoutine = launch { receiveMessages() }
			val sendMessagesRoutine = launch { sendMessages(inputs) }

			sendMessagesRoutine.join()
			responseMessageRoutine.cancelAndJoin()
		}
	}

	private suspend fun DefaultClientWebSocketSession.receiveMessages() {
		while (true) _responses.emit(receiveDeserialized())
	}

	private suspend fun DefaultClientWebSocketSession.sendMessages(inputs: Flow<RpcRequest<*>>) {
		inputs.collect { sendSerialized(it) } // TODO make this cancellable
	}
}
