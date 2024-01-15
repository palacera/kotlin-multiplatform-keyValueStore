package cache

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.CoroutineScope

data class CacheConfig(
    val name: String,
    val file: String,
    val isTemporary: Boolean = true,
    val strategy: CacheStrategy = CacheStrategy.FirstInFirstOut,
    val maxEntries: Long = 100,
    val defaultExpireTime: Duration = 1.days
)
