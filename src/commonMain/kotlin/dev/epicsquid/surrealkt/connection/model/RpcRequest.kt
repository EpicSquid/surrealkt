package dev.epicsquid.surrealkt.connection.model

import kotlinx.serialization.Serializable

interface RpcRequest {
	val id: String
	val method: String
}

@Serializable
data class DefaultRpcRequest(
	override val id: String,
	override val method: String
) : RpcRequest