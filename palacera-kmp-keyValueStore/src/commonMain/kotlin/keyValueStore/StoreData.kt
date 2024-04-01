package keyValueStore

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class StoreData<T>(
    val data: T,
    val createdAt: Instant,
    val expiresIn: Duration = Duration.INFINITE,
) {
    fun isExpired(): Boolean =
        when (expiresIn) {
            Duration.ZERO -> true
            Duration.INFINITE -> false
            else -> createdAt + expiresIn < Clock.System.now()
        }
}

fun <T : Any> storeData(
    data: T,
    expiresIn: Duration,
) = StoreData(data, Clock.System.now(), expiresIn)
