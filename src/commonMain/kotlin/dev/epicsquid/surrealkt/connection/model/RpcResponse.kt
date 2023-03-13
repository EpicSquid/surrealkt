package dev.epicsquid.surrealkt.connection.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class RpcResponse(
	val id: String,
	val response: JsonElement,
	val error: Error?
)

inline fun <reified T> RpcResponse.decodeResult(): T = Json.decodeFromJsonElement(response)