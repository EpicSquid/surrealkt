package dev.epicsquid.surrealkt.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

object QueryResponseSerializer: KSerializer<List<JsonElement>> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RpcResponse") {
        element("status", String.serializer().descriptor)
        element("result", JsonElement.serializer().descriptor)
        element("time", String.serializer().descriptor)
        element("error", String.serializer().descriptor)
    }

	override fun deserialize(decoder: Decoder): List<JsonElement>{
		var result: List<JsonElement>? = null
		var error: String? = null
		var status: String? = null
		decoder.decodeStructure(descriptor){
			while (true){
				when(decodeElementIndex(descriptor)){
					0 -> status = decodeStringElement(descriptor, 0)
					1 ->  result = decodeSerializableElement(descriptor, 1, ListSerializer(JsonElement.serializer()))
					2 -> error = decodeSerializableElement(descriptor, 2, String.serializer())
					3 -> decodeSerializableElement(descriptor, 3, Duration.serializer())
					CompositeDecoder.DECODE_DONE -> break
				}
			}
		}
		when(status) {
			null -> error("No status found for response")
			"ERR" -> throw Exception("Surreal query failed with error: '$error'")
		}
		return result!!
	}

	override fun serialize(encoder: Encoder, value: List<JsonElement>) {
		TODO("Not yet implemented")
	}
}