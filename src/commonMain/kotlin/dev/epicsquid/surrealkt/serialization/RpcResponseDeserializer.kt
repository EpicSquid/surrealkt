package dev.epicsquid.surrealkt.serialization

import dev.epicsquid.surrealkt.connection.model.Error
import dev.epicsquid.surrealkt.connection.model.RpcResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonElement

object RpcResponseDeserializer: KSerializer<RpcResponse> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RpcResponse") {
        element("id", String.serializer().descriptor)
        element("result", JsonElement.serializer().descriptor)
        element("error", String.serializer().descriptor)
    }

	override fun deserialize(decoder: Decoder): RpcResponse {
		var id: String? = null
		var result: JsonElement? = null
		var error: Error? = null
		decoder.decodeStructure(descriptor){
			while (true){
				when(decodeElementIndex(descriptor)){
					0 -> id = decodeStringElement(descriptor, 1)
					1 ->  { result = decodeSerializableElement(descriptor, 2, JsonElement.serializer()) }
					2 -> error = decodeSerializableElement(descriptor, 3, Error.serializer())
					CompositeDecoder.DECODE_DONE -> break
				}
			}
		}
		if(id == null) error("No id found for response")
		if (error != null) return RpcResponse.Error(id!!, error!!)
		return RpcResponse.Success(id!!, result!!)
	}

	override fun serialize(encoder: Encoder, value: RpcResponse) {
		TODO("Not yet implemented")
	}

}