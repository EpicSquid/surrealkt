package dev.epicsquid.surrealkt.connection.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RpcResponse(
	val id: String,
	val error: Error?,
	val element: JsonElement
)