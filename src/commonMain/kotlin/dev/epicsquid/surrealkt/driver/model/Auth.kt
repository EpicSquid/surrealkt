package dev.epicsquid.surrealkt.driver.model

import kotlinx.serialization.Serializable

sealed interface Auth

@Serializable
data class RootAuth(
	val user: String,
	val pass: String
) : Auth

@Serializable
data class NamespaceAuth(
	val NS: String,
	val user: String,
	val pass: String
) : Auth

@Serializable
data class DatabaseAuth(
	val NS: String,
	val DB: String,
	val user: String,
	val pass: String
) : Auth

@Serializable
data class ScopeAuth(
	val NS: String,
	val DB: String,
	val SC: String
) : Auth