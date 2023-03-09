package dev.epicsquid.surrealkt.driver.model

data class QueryResult<T>(
	val result: List<T>,
	val status: String,
	val time: String
)
