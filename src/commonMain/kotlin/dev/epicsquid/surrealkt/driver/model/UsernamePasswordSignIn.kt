package dev.epicsquid.surrealkt.driver.model

import kotlinx.serialization.Serializable

@Serializable
data class UsernamePasswordSignIn(
	val user: String,
	val pass: String
)
