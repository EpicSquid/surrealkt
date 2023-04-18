package dev.epicsquid.surrealkt.connection.model

import dev.epicsquid.surrealkt.serialization.SurrealJson
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class RpcResponse(
) {
	abstract val id: String
	@Serializable
	class Success(
		override val id: String,
		val response: JsonElement,
	): RpcResponse()

	@Serializable
	class Error(
		override val id: String,
		val error: dev.epicsquid.surrealkt.connection.model.Error?
	): RpcResponse()
}


inline fun <reified T> RpcResponse.decodeResult(deserializationStrategy: DeserializationStrategy<T>): T = when(this) {
	is RpcResponse.Success -> SurrealJson.decodeFromJsonElement(deserializationStrategy, response)
	is RpcResponse.Error -> throw Exception("SurrealDB returned an error: '$error'")
}

