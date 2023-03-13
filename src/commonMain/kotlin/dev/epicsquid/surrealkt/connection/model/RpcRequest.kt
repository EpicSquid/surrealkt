package dev.epicsquid.surrealkt.connection.model

import kotlinx.serialization.Serializable

@Serializable
data class RpcRequest<T>(
	val id: String,
	val method: String,
	val params: List<T>? = null
)