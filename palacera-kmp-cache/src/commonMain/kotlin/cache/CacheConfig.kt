package cache

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class CacheConfig(
    val name: String,
    val file: String = name,
    val strategy: CacheStrategy = CacheStrategy.FirstInFirstOut,
    val maxEntries: Long = 100,
    val expirationEnabled: Boolean = true,
    val minExpireDuration: Duration = 15.minutes,
    val maxExpireDuration: Duration = Duration.INFINITE,
)
