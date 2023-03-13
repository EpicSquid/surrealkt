package dev.epicsquid.surrealkt.driver.model

import kotlinx.serialization.Serializable

@Serializable
data class QueryResult<T>(
	val result: List<T>,
	val status: String,
	val time: String
)
