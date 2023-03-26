package dev.epicsquid.surrealkt.driver

import dev.epicsquid.surrealkt.connection.SurrealWebsocketConnection
import dev.epicsquid.surrealkt.connection.model.RpcRequest
import dev.epicsquid.surrealkt.connection.model.decodeResult
import dev.epicsquid.surrealkt.driver.model.Auth
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.encodeToJsonElement

class SurrealDriver(
	val connection: SurrealWebsocketConnection,
) {

	private var requestId: Long = 0

	suspend fun ping(): Flow<Boolean> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "ping"
		)
	) { it.decodeResult() }

	suspend fun use(namespace: String, database: String) {
		connection.rawRpc(
			RpcRequest(
				id = genId(),
				method = "use",
				params = buildJsonArray {
					add(namespace)
					add(database)
				}
			)
		)
	}

	suspend fun signUp(auth: Auth): Flow<String> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "signup",
			params = buildJsonArray {
				add(Json.encodeToJsonElement(auth))
			}
		)
	) { it.decodeResult() }

	suspend fun signIn(auth: Auth): Flow<String> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "signin",
			params = buildJsonArray {
				add(Json.encodeToJsonElement(auth))
			}
		)
	) { it.decodeResult() }

	// TODO work out how to handle the reified part without having to make connection public
	suspend inline fun <reified T> let(key: String, value: T): Flow<String> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "let",
			params = buildJsonArray {
				add(key)
				add(Json.encodeToJsonElement(value))
			}
		)
	) { it.decodeResult() }

	suspend inline fun <reified T, reified V> query(query: String, vars: V): Flow<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "query",
			params = buildJsonArray {
				add(Json.encodeToJsonElement(vars))
			}
		)
	) { it.decodeResult() }

	suspend inline fun <reified T> select(thing: String): Flow<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "select",
			params = buildJsonArray {
				add(thing)
			}
		)
	) { it.decodeResult() }

	suspend inline fun <reified T, reified D> create(thing: String, data: D): Flow<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "create",
			params = buildJsonArray {
				add(thing)
				add(Json.encodeToJsonElement(data))
			}
		)
	) { it.decodeResult() }

	suspend inline fun <reified T, reified D> update(thing: String, data: D): Flow<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "update",
			params = buildJsonArray {
				add(thing)
				add(Json.encodeToJsonElement(data))
			}
		)
	) { it.decodeResult() }

	suspend inline fun <reified T, reified D> change(thing: String, data: D): Flow<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "change",
			params = buildJsonArray {
				add(thing)
				add(Json.encodeToJsonElement(data))
			}
		)
	) { it.decodeResult() }

	suspend inline fun <reified T> delete(thing: String): Flow<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "delete",
			params = buildJsonArray {
				add(thing)
			}
		)
	) { it.decodeResult() }

	fun genId(): String = requestId++.toString() // TODO make this a better system using uuids or something
}