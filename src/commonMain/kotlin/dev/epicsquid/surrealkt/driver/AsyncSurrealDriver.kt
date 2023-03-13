package dev.epicsquid.surrealkt.driver

import dev.epicsquid.surrealkt.connection.SurrealWebsocketConnection
import dev.epicsquid.surrealkt.connection.model.DefaultRpcRequest
import dev.epicsquid.surrealkt.connection.model.RpcResponse
import kotlinx.coroutines.flow.Flow

class AsyncSurrealDriver(
	private val connection: SurrealWebsocketConnection,
) {

	private var requestId: Long = 0

	suspend fun ping(): Flow<RpcResponse> = connection.rawRpc(
		DefaultRpcRequest(
			id = requestId++.toString(),
			method = "ping"
		)
	)
}