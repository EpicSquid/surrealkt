package dev.epicsquid.surrealkt.connection

import dev.epicsquid.surrealkt.connection.model.RpcRequest
import dev.epicsquid.surrealkt.connection.model.RpcResponse
import dev.epicsquid.surrealkt.serialization.RpcResponseDeserializer
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SurrealWebsocketConnection(
	private val host: String,
	private val port: Int = 8000,
	private val client: HttpClient = client()
) {
	internal val channels = ConcurrentMap<String, Channel<RpcResponse>>()
	private lateinit var websocket: DefaultClientWebSocketSession
	private val scope = CoroutineScope(Dispatchers.Default)

	suspend fun connect() {
		websocket = client.webSocketSession(method = HttpMethod.Get, host = host, port = port, path = "/rpc")
		with(websocket){
			scope.launch {
				receiveMessages()
			}
		}
	}

	private suspend fun DefaultClientWebSocketSession.receiveMessages() {
		while (true) {
			val frame = incoming.receive() as Frame.Text
			val response = Json.decodeFromString(RpcResponseDeserializer, frame.readText())
			channels[response.id]?.send(response) ?: throw Exception("Response '${response.id}' doesn't match any request sent")
		}
	}

	private suspend fun sendMessage(message: RpcRequest): RpcResponse {
		val response = Channel<RpcResponse>(1)
		channels[message.id] = response
		val data = Json.encodeToString(RpcRequest.serializer(), message)
		websocket.send(data)
		return response.receive().also { channels.remove(message.id) }
	}

	private suspend fun openMessageChannel(message: RpcRequest): Channel<RpcResponse> {
		val response = Channel<RpcResponse>(1)
		channels[message.id] = response
		val data = Json.encodeToString(RpcRequest.serializer(), message)
		websocket.send(data)
		return response
	}

	suspend fun <T> rpc(request: RpcRequest, mapper: (RpcResponse) -> T) : T {
		return mapper(sendMessage(request))
	}

	suspend fun <T> rpcChannel(request: RpcRequest, mapper: (RpcResponse) -> T) : Flow<T> {
		return openMessageChannel(request)
			.receiveAsFlow()
			.map(mapper)
	}
	suspend fun rawRpc(request: RpcRequest) : Flow<RpcResponse> = rpcChannel(request) { it }
}
