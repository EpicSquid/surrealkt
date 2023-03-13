package dev.epicsquid.surrealkt.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

class SingleToListSerializer<T>(serializer: KSerializer<T>) :
	JsonTransformingSerializer<List<T>>(ListSerializer(serializer)) {

	override fun transformDeserialize(element: JsonElement): JsonElement =
		if (element !is JsonArray) JsonArray(listOf(element)) else element
}