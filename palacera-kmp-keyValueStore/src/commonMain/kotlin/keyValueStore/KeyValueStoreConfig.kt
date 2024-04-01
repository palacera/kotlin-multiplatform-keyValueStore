package keyValueStore

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class KeyValueStoreConfig(
    val name: String,
    val file: String = name,
    val purgeStrategy: PurgeStrategy = PurgeStrategy.FirstInFirstOut,
    val maxEntries: Long = 100,
    val expirationDuration: Duration = 1.hours,
) {
    init {
        require(maxEntries > 0) { "maxEntries must be greater than 0" }
    }
}
