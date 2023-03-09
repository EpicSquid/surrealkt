package dev.epicsquid.surrealkt.driver.model

interface Patch

data class AddPatch(
	val op: String = "add",
	val path: String,
	val value: String
) : Patch

data class ChangePatch(
	val op: String = "change",
	val path: String,
	val value: String
) : Patch

data class RemovePatch(
	val op: String = "remove",
	val path: String
) : Patch

data class ReplacePatch(
	val op: String = "replace",
	val path: String,
	val value: String
) : Patch