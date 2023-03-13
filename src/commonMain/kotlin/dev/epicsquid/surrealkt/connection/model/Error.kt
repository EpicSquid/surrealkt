package dev.epicsquid.surrealkt.connection.model

import kotlinx.serialization.Serializable

@Serializable
data class Error(
	val code: Int,
	val message: String
)