package dev.epicsquid.surrealkt.driver.model

import kotlinx.serialization.Serializable

interface Patch

@Serializable
data class AddPatch(
	val op: String = "add",
	val path: String,
	val value: String
) : Patch

@Serializable
data class ChangePatch(
	val op: String = "change",
	val path: String,
	val value: String
) : Patch

@Serializable
data class RemovePatch(
	val op: String = "remove",
	val path: String
) : Patch

@Serializable
data class ReplacePatch(
	val op: String = "replace",
	val path: String,
	val value: String
) : Patch