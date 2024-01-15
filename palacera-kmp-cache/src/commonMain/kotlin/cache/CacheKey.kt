package cache

import kotlin.jvm.JvmInline

@JvmInline
value class CacheKey(val key: String)

fun cacheKey(vararg segments: String): CacheKey = CacheKey(segments.joinToString(separator = ":"))
