package dev.epicsquid.surrealkt.serialization

import dev.epicsquid.surrealkt.driver.model.*
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

object AuthSerializer: SerializationStrategy<Auth> {
	override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

	override fun serialize(encoder: Encoder, value: Auth) {
		when(value) {
			is RootAuth -> encoder.encodeSerializableValue(RootAuth.serializer(), value)
			is NamespaceAuth -> encoder.encodeSerializableValue(NamespaceAuth.serializer(), value)
			is DatabaseAuth -> encoder.encodeSerializableValue(DatabaseAuth.serializer(), value)
			is ScopeAuth -> encoder.encodeSerializableValue(ScopeAuth.serializer(), value)
		}
	}

}