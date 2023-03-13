package dev.epicsquid.surrealkt.connection

import dev.epicsquid.surrealkt.connection.model.RpcRequest
import dev.epicsquid.surrealkt.connection.model.RpcResponse
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SurrealWebsocketConnection(
	private val host: String,
	private val port: Int = 8000,
	private val client: HttpClient = client()
) {
	private val requests: MutableSharedFlow<RpcRequest<*>> = MutableSharedFlow()
	private val responses: MutableSharedFlow<RpcResponse> = MutableSharedFlow()

	suspend fun connect() {
		client.webSocket(method = HttpMethod.Get, host = host, port = port, path = "/rpc") {
			val responseMessageRoutine = launch { receiveMessages() }
			val sendMessagesRoutine = launch { sendMessages() }

			sendMessagesRoutine.join()
			responseMessageRoutine.cancelAndJoin()
		}
	}

	private suspend fun DefaultClientWebSocketSession.receiveMessages() {
		while (true) responses.emit(receiveDeserialized())
	}

	private suspend fun DefaultClientWebSocketSession.sendMessages() {
		requests.collect { sendSerialized(it) } // TODO make this cancellable
	}

	suspend fun <T> rpc(request: RpcRequest<*>, mapper: (RpcResponse) -> T) : Flow<T> {
		requests.emit(request)
		return responses.filter { it.id == request.id }.map(mapper)
	}

	suspend fun rawRpc(request: RpcRequest<*>) : Flow<RpcResponse> = rpc(request) { it }
}
