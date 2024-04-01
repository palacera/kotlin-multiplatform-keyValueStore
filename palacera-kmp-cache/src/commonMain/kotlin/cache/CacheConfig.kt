package cache

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class CacheConfig(
    val name: String,
    val file: String = name,
    val strategy: CacheStrategy = CacheStrategy.FirstInFirstOut,
    val maxEntries: Long = 100,
    val expirationDuration: Duration = 1.hours,
) {
    init {
        require(maxEntries > 0) { "maxEntries must be greater than 0" }
    }
}
