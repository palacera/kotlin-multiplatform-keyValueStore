package keyValueStore

import kotlin.jvm.JvmInline

@JvmInline
value class StoreKey(val key: String)

fun storeKey(vararg segments: String): StoreKey = StoreKey(segments.joinToString(separator = ":"))
