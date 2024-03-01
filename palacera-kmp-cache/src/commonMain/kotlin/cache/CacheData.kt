package cache

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CacheData<T>(
    val data: T,
    val cachePolicy: CachePolicy,
    val createdAt: Instant,
) {
    fun isExpired(): Boolean =
        when (cachePolicy) {
            is CachePolicy.Never -> true
            is CachePolicy.UntilExpires -> createdAt + cachePolicy.duration < Clock.System.now()
            else -> false
        }
}

fun <T : Any> cacheData(
    data: T,
    cachePolicy: CachePolicy,
) = CacheData(data, cachePolicy, Clock.System.now())
