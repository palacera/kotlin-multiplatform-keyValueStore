package cache

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface CachePolicy {
    @Serializable
    data object Never : CachePolicy // Request data, do not cache

    @Serializable
    data class Refresh(val expiresIn: Duration = Duration.INFINITE) : CachePolicy // Request data, then update cache

    @Serializable
    data object IfAvailable : CachePolicy // Return cache if available, else request data

    @Serializable
    data class UntilExpires(val expiresIn: Duration) : CachePolicy // Return cache if not expired, else request data
}
