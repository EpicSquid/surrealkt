package dev.epicsquid.surrealkt.driver

import dev.epicsquid.surrealkt.connection.SurrealWebsocketConnection
import dev.epicsquid.surrealkt.serialization.QueryResponseSerializer
import dev.epicsquid.surrealkt.connection.model.RpcRequest
import dev.epicsquid.surrealkt.connection.model.RpcResponse
import dev.epicsquid.surrealkt.connection.model.decodeResult
import dev.epicsquid.surrealkt.driver.model.Auth
import dev.epicsquid.surrealkt.serialization.AuthSerializer
import dev.epicsquid.surrealkt.serialization.SingleToListSerializer
import dev.epicsquid.surrealkt.serialization.SurrealJson
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

class SurrealDriver(
	val connection: SurrealWebsocketConnection,
) {
	private var requestId: Long = 0

	suspend fun ping(): Boolean = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "ping",
			params = JsonArray(listOf())
		)
	) { it.decodeResult(Boolean.serializer()) }

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

	suspend fun signUp(auth: Auth): String = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "signup",
			params = buildJsonArray {
				add(SurrealJson.encodeToJsonElement(AuthSerializer, auth))
			}
		)
	) { it.decodeResult(String.serializer()) }

	suspend fun signIn(auth: Auth): String {
		val id = genId()
		return connection.rpc(
			RpcRequest(
				id = id,
				method = "signin",
				params = buildJsonArray {
					add(SurrealJson.encodeToJsonElement(AuthSerializer, auth))
				}
			)
		) { it.decodeResult(String.serializer()) }
	}

	// TODO work out how to handle the reified part without having to make connection public
	suspend inline fun <reified T> let(key: String, value: T): String {
		val id = genId()
		return connection.rpc(
			RpcRequest(
				id = id,
				method = "let",
				params = buildJsonArray {
					add(key)
					add(SurrealJson.encodeToJsonElement(value))
				}
			)
		) { it.decodeResult(String.serializer()) }
	}

	suspend inline fun <reified T> query(query: String): List<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "query",
			params = buildJsonArray {
				add(query)
			}
		)
	) {
		val results = SurrealJson.decodeFromJsonElement(SingleToListSerializer(QueryResponseSerializer) , (it as RpcResponse.Success).response)
		results.last().map {  SurrealJson.decodeFromJsonElement(it)  }
	}

			/*

	suspend inline fun <reified T, reified V> query(query: String, vars: V): List<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "query",
			params = buildJsonArray {
				add(SurrealJson.encodeToJsonElement(vars))
			}
		)
	) { SurrealJson.decodeFromJsonElement(SurrealJson.decodeFromJsonElement(QueryResponseDeserializationStrategies, it.decodeResult(JsonElement.serializer()))) }

		 */

	suspend inline fun <reified T> select(thing: String): List<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "select",
			params = buildJsonArray {
				add(thing)
			}
		)
	) { SurrealJson.decodeFromJsonElement(it.decodeResult(JsonElement.serializer())) }

	suspend inline fun <reified T, reified D> create(thing: String, data: D): T = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "create",
			params = buildJsonArray {
				add(thing)
				add(SurrealJson.encodeToJsonElement(data))
			}
		)
	) { SurrealJson.decodeFromJsonElement(it.decodeResult(SingleToListSerializer(JsonElement.serializer())).first()) }

	suspend inline fun <reified T, reified D> update(thing: String, data: D): T = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "update",
			params = buildJsonArray {
				add(thing)
				add(SurrealJson.encodeToJsonElement(data))
			}
		)
	) { SurrealJson.decodeFromJsonElement(it.decodeResult(JsonElement.serializer())) }

	suspend inline fun <reified T, reified D> change(thing: String, data: D): List<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "change",
			params = buildJsonArray {
				add(thing)
				add(SurrealJson.encodeToJsonElement(data))
			}
		)
	) { SurrealJson.decodeFromJsonElement(it.decodeResult(JsonElement.serializer())) }

	suspend inline fun <reified T> delete(thing: String): List<T> = connection.rpc(
		RpcRequest(
			id = genId(),
			method = "delete",
			params = buildJsonArray {
				add(thing)
			}
		)
	) { SurrealJson.decodeFromJsonElement(it.decodeResult(JsonElement.serializer())) }

	fun genId(): String = requestId++.toString() // TODO make this a better system using uuids or something
}