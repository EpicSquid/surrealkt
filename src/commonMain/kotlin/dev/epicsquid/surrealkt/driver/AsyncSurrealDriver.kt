package dev.epicsquid.surrealkt.driver

import dev.epicsquid.surrealkt.connection.SurrealWebsocketConnection
import dev.epicsquid.surrealkt.connection.model.RpcRequest
import dev.epicsquid.surrealkt.connection.model.decodeResult
import dev.epicsquid.surrealkt.driver.model.Auth
import kotlinx.coroutines.flow.Flow

class AsyncSurrealDriver(
	private val connection: SurrealWebsocketConnection,
) {

	private var requestId: Long = 0

	suspend fun ping(): Flow<Boolean> = connection.rpc(
		RpcRequest<Nothing>(
			id = genId(),
			method = "ping"
		)
	) { it.decodeResult() }

	suspend fun use(namespace: String, database: String) {
		connection.rawRpc(
			RpcRequest(
				id = genId(),
				method = "use",
				params = listOf(namespace, database)
			)
		)
	}

	suspend fun signUp(auth: Auth): Flow<String> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "signup",
			params = listOf(auth)
		)
	) { it.decodeResult() }

	suspend fun signIn(auth: Auth): Flow<String> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "signin",
			params = listOf(auth)
		)
	) { it.decodeResult() }


	private fun genId(): String = requestId++.toString()
}